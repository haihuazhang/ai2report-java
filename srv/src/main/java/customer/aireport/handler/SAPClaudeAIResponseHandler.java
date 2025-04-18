package customer.aireport.handler;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import customer.aireport.adapter.SAPClaudeAIResponseAdapter;
import customer.aireport.dto.AIResponse;
import customer.aireport.exception.BusinessException;
import customer.aireport.service.SAPAICore.claude.generated.model.InvokeResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseResponse;

@Component
public class SAPClaudeAIResponseHandler implements AIResponseHandler {

    private final ObjectMapper objectMapper;

    public SAPClaudeAIResponseHandler() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Processes the raw response from the AI service and converts it into a standard AIResponse object.
     *
     * @param rawResponse The raw response object from the AI service, which can be either an InvokeResponse or ConverseResponse.
     * @return An AIResponse object containing the processed response data.
     * @throws IllegalArgumentException if the raw response is neither an InvokeResponse nor a ConverseResponse.
     */
    @Override
    public AIResponse processResponse(Object rawResponse) {
        if (rawResponse instanceof InvokeResponse) {
            return new SAPClaudeAIResponseAdapter((InvokeResponse) rawResponse);
        } else if (rawResponse instanceof ConverseResponse) {
            try {
                return new SAPClaudeAIResponseAdapter((ConverseResponse) rawResponse, objectMapper);
            } catch (JsonProcessingException e) {
                // throw e;
                throw new BusinessException("null",e);
            }
        }

        throw new IllegalArgumentException("Expected InvokeResponse or ConverseResponse but got: " 
            + rawResponse.getClass().getName());
    }
}