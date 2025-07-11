package org.wso2.samples;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.WorkflowResponse;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.Identifier;
import org.wso2.carbon.apimgt.api.model.Subscriber;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.WorkflowDTO;
import org.wso2.carbon.apimgt.impl.token.ClaimsRetriever;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.impl.workflow.APIStateChangeSimpleWorkflowExecutor;
import org.wso2.carbon.apimgt.impl.workflow.APIStateWorkflowDTO;
import org.wso2.carbon.apimgt.impl.workflow.WorkflowException;
import org.wso2.carbon.apimgt.persistence.APIPersistence;
import org.wso2.carbon.apimgt.persistence.dto.PublisherAPI;
import org.wso2.carbon.apimgt.persistence.exceptions.APIPersistenceException;
import org.wso2.carbon.apimgt.impl.factory.PersistenceFactory;
import org.wso2.carbon.apimgt.persistence.dto.Organization;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;


import org.apache.axis2.util.URL;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

public class PromoteWorkflowExecutor extends APIStateChangeSimpleWorkflowExecutor {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(PromoteWorkflowExecutor.class);

    @Override
    public WorkflowResponse execute(WorkflowDTO workflowDTO) throws WorkflowException {

        Map<Integer, Integer> subscriberMap = new HashMap<>();
        APIStateWorkflowDTO apiStateWorkFlowDTO = (APIStateWorkflowDTO) workflowDTO;
        APIPersistence apiPersistenceInstance = PersistenceFactory.getAPIPersistenceInstance();
        
        //Imprime la acción siguiente del ciclo de vida del API, el boton que ha presionado!
        String nextAction= apiStateWorkFlowDTO.getApiLCAction();
        log.error("PromoteWorkflowExecutor: Next Action: " + nextAction);

        if(nextAction!=null && "Promoted".equalsIgnoreCase(nextAction.trim())){
            log.error("Eligio la accion siguiente: " + nextAction);
        }
        Organization org = new Organization(apiStateWorkFlowDTO.getTenantDomain());
        PublisherAPI publisherAPI;
        
        try {
            publisherAPI = apiPersistenceInstance.getPublisherAPI(org, apiStateWorkFlowDTO.getApiUUID());
            log.error("Technnical OWNER: " + publisherAPI.getTechnicalOwner());
            
        } catch(APIPersistenceException e) {
            String errorMsg = "Error while retrieving API from persistence layer";
            log.error(errorMsg, e);
            throw new WorkflowException(errorMsg, e);
        }

        if (publisherAPI != null) {
            log.error("Entro Publisher API: ");
        }
        if ("PROMOTED".equals(apiStateWorkFlowDTO.getApiCurrentState())) {
            log.error("Current State: " + nextAction);
         }
        if(nextAction!=null && "Promoted".equalsIgnoreCase(nextAction.trim())){
            log.error("Eligio la accion siguiente: " + nextAction);
        }
        if (nextAction!=null && "Promoted".equalsIgnoreCase(nextAction.trim())) {

            URL serviceEndpointURL = new URL("https://pablo.requestcatcher.com/test");
            HttpClient httpClient = APIUtil.getHttpClient(serviceEndpointURL.getPort(),serviceEndpointURL.getProtocol());
            HttpPost httpPost = new HttpPost("https://pablo.requestcatcher.com/test"+"?name="+apiStateWorkFlowDTO.getApiName()
                                            +"&version="+apiStateWorkFlowDTO.getApiVersion());

            try {
                HttpResponse response = httpClient.execute(httpPost);
                String respuesta=response.getStatusLine().getStatusCode()+ " " + response.getStatusLine().getReasonPhrase();
                log.error("RESPUESTA: "+respuesta);
            
             } catch (ClientProtocolException e) {
                String errorMsg = "Error while creating the http client";
                log.error(errorMsg, e);
                throw new WorkflowException(errorMsg, e);
            } catch (IOException e) {
                String errorMsg = "Error process...";
                log.error(errorMsg, e);
                throw new WorkflowException(errorMsg, e);
            }
        }

        return super.execute(workflowDTO);

    }
}