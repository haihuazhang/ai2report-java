package customer.aireport.handler;

import org.springframework.stereotype.Component;

import customer.aireport.adapter.SAPClaudeAIResponseAdapter;
import customer.aireport.dto.AIResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.InvokeResponse;

@Component
public class SAPClaudeAIResponseHandler implements AIResponseHandler {
    
    @Override
    public AIResponse processResponse(Object rawResponse) {
        if (!(rawResponse instanceof InvokeResponse)) {
            throw new IllegalArgumentException("Expected OpenAiChatCompletionOutput but got: " 
                + rawResponse.getClass().getName());
        }
        
        return new SAPClaudeAIResponseAdapter((InvokeResponse) rawResponse);
    }
}