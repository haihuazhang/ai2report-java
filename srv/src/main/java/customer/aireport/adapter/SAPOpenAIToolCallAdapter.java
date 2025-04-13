package customer.aireport.adapter;

import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatToolCall;
import customer.aireport.dto.AIFunction;
import customer.aireport.dto.AIToolCall;

public class SAPOpenAIToolCallAdapter implements AIToolCall {
    private final OpenAiChatToolCall toolCall;

    public SAPOpenAIToolCallAdapter(OpenAiChatToolCall toolCall) {
        this.toolCall = toolCall;
    }

    @Override
    public AIFunction getFunction() {
        return new SAPOpenAIFunctionAdapter(toolCall.getFunction());
    }
}