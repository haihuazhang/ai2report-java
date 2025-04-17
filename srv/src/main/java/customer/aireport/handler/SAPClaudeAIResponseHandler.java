package customer.aireport.handler;

import org.springframework.stereotype.Component;

import customer.aireport.adapter.SAPClaudeAIResponseAdapter;
import customer.aireport.dto.AIResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.InvokeResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseResponse;

@Component
public class SAPClaudeAIResponseHandler implements AIResponseHandler {
    
    @Override
    public AIResponse processResponse(Object rawResponse) {
        if (rawResponse instanceof InvokeResponse) {
            return new SAPClaudeAIResponseAdapter((InvokeResponse) rawResponse);
        } else if (rawResponse instanceof ConverseResponse) {
            return new SAPClaudeAIResponseAdapter((ConverseResponse) rawResponse);
        }

        throw new IllegalArgumentException("Expected InvokeResponse or ConverseResponse but got: " 
            + rawResponse.getClass().getName());
    }
}