package customer.aireport.handler;

import org.springframework.stereotype.Component;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionOutput;
import customer.aireport.adapter.SAPAIResponseAdapter;
import customer.aireport.dto.AIResponse;

@Component
public class SAPAIResponseHandler implements AIResponseHandler {
    
    @Override
    public AIResponse processResponse(Object rawResponse) {
        if (!(rawResponse instanceof OpenAiChatCompletionOutput)) {
            throw new IllegalArgumentException("Expected OpenAiChatCompletionOutput but got: " 
                + rawResponse.getClass().getName());
        }
        
        return new SAPAIResponseAdapter((OpenAiChatCompletionOutput) rawResponse);
    }
}