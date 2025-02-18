package customer.aireport.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage;
// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage.OpenAiChatAssistantMessage;
// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage.OpenAiChatFunctionMessage;
// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage.OpenAiChatSystemMessage;
// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage.OpenAiChatToolMessage;
// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage.OpenAiChatUserMessage;
// import com.fasterxml.jackson.annotation.JsonSubTypes;
// import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
// import com.fasterxml.jackson.annotation.JsonTypeInfo;
// import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class OpenAiChatAssistantMessage2 implements OpenAiChatMessage{
    @JsonProperty("role")
    @Getter(onMethod_ = @Nonnull)
    private final String role = "assistant";
    /** The contents of the system message. */
    @JsonProperty("content")
    @Getter(onMethod_ = @Nullable)
    @Setter(onParam_ = @Nonnull)
    private String content;
}
