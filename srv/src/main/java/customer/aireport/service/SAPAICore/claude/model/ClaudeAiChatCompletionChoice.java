package customer.aireport.service.SAPAICore.claude.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;
import customer.aireport.service.SAPAICore.claude.model.ClaudeAiChatMessage.ClaudeAiChatAssistantMessage;
import javax.annotation.Nonnull;
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
public class ClaudeAiChatCompletionChoice extends ClaudeAiCompletionChoice {
  /** Completion chat message. */
  @JsonProperty("message")
  @Getter(onMethod_ = @Nonnull)
  @Setter(onMethod_ = @Nonnull, value = AccessLevel.PACKAGE)
  private ClaudeAiChatAssistantMessage message;

  void addDelta(@Nonnull final ClaudeAiDeltaChatCompletionChoice delta) {
    super.addDelta(delta);

    if (delta.getMessage() != null) {
      if (message == null) {
        message = new ClaudeAiChatAssistantMessage();
      }
      message.addDelta(delta.getMessage());
    }
  }
}
