package customer.aireport.handler;

import org.springframework.stereotype.Component;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionOutput;
import customer.aireport.adapter.SAPOpenAIResponseAdapter;
import customer.aireport.dto.AIResponse;

@Component
public class SAPOpenAIResponseHandler implements AIResponseHandler {
    
    @Override
    public AIResponse processResponse(Object rawResponse) {
        if (!(rawResponse instanceof OpenAiChatCompletionOutput)) {
            throw new IllegalArgumentException("Expected OpenAiChatCompletionOutput but got: " 
                + rawResponse.getClass().getName());
        }
        
        return new SAPOpenAIResponseAdapter((OpenAiChatCompletionOutput) rawResponse);
    }
}