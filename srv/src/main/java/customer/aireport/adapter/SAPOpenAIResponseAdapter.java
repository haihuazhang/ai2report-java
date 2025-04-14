package customer.aireport.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionOutput;
import customer.aireport.dto.AIResponse;
// ...other imports
import customer.aireport.dto.AIToolCall;

@Component
public class SAPOpenAIResponseAdapter implements AIResponse {
    private final OpenAiChatCompletionOutput output;

    public SAPOpenAIResponseAdapter(OpenAiChatCompletionOutput output) {
        this.output = output;
    }

    public String getFinishReason() {
        return output.getChoices().get(0).getFinishReason();
    }

    @Override
    public String getContent() {
        // return output.getContent();
        if ("tool_calls".equals(this.getFinishReason())){
             return this.getToolCalls().get(0).getFunction().getArguments();
        } else {
            return output.getContent();
        }
    }

    public List<AIToolCall> getToolCalls() {
        // Convert OpenAI tool calls to our interface
        return output.getChoices().get(0).getMessage().getToolCalls().stream()
            .map(SAPOpenAIToolCallAdapter::new)
            .collect(Collectors.toList());
    }
}