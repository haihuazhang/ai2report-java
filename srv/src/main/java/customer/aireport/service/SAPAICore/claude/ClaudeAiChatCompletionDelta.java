package customer.aireport.service.SAPAICore.claude;

import static customer.aireport.service.SAPAICore.claude.ClaudeAiUtils.getClaudeAiObjectMapper;
import static lombok.AccessLevel.PACKAGE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.annotations.Beta;
import com.sap.ai.sdk.core.common.StreamedDelta;
import customer.aireport.service.SAPAICore.claude.generated.model.CompletionUsage;
import customer.aireport.service.SAPAICore.claude.generated.model.CreateChatCompletionStreamResponse;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents an OpenAI chat completion output delta for streaming.
 *
 * @since 1.4.0
 */
@Beta
@RequiredArgsConstructor(onConstructor_ = @JsonCreator, access = PACKAGE)
@Getter
@ToString
@EqualsAndHashCode
public class ClaudeAiChatCompletionDelta implements StreamedDelta {
  /** The original response from the chat completion stream. */
  @Nonnull private final CreateChatCompletionStreamResponse originalResponse;

  @Nonnull
  @Override
  public String getDeltaContent() {
    final var choices = getOriginalResponse().getChoices();
    if (!choices.isEmpty() && choices.get(0).getIndex() == 0) {
      final var message = choices.get(0).getDelta();
      return Objects.requireNonNullElse(message.getContent(), "");
    }
    return "";
  }

  @Nullable
  @Override
  public String getFinishReason() {
    final var choices = getOriginalResponse().getChoices();
    if (!choices.isEmpty()) {
      final var finishReason = choices.get(0).getFinishReason();
      return finishReason != null ? finishReason.getValue() : null;
    }
    return null;
  }

  /**
   * Retrieves the completion usage from the response, or null if it is not available.
   *
   * @return The completion usage or null.
   */
  @Nullable
  public CompletionUsage getCompletionUsage() {
    if (getOriginalResponse().getCustomFieldNames().contains("usage")
        && getOriginalResponse().getCustomField("usage") instanceof Map<?, ?> usage) {
      return getClaudeAiObjectMapper().convertValue(usage, CompletionUsage.class);
    }
    return null;
  }
}
