package customer.aireport.service.SAPAICore.claude.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;
import customer.aireport.service.SAPAICore.claude.model.ClaudeAiChatMessage.ClaudeAiChatAssistantMessage;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/** Result candidates for OpenAI chat completion output. */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Beta
public class ClaudeAiDeltaChatCompletionChoice extends ClaudeAiCompletionChoice {
  /** Completion chat message. */
  @JsonProperty("delta")
  @Getter(onMethod_ = @Nullable)
  @Setter(onMethod_ = @Nullable, value = AccessLevel.PACKAGE)
  private ClaudeAiChatAssistantMessage message;
}
