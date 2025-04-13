package customer.aireport.adapter;

import customer.aireport.dto.AIFunction;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatFunctionCall;

public class SAPOpenAIFunctionAdapter implements AIFunction {
    private final OpenAiChatFunctionCall function;

    public SAPOpenAIFunctionAdapter(OpenAiChatFunctionCall function) {
        this.function = function;
    }

    @Override
    public String getArguments() {
        return function.getArguments();
    }

    @Override
    public String getName() {
        return function.getName();
    }
}
