package customer.aireport.model;

// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionFunction;
import lombok.Value;

@Value
public class AIParameters<T> {
    T function;
    String promptContent;
}