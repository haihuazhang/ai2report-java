package customer.aireport.service.SAPAICore.claude;

import static customer.aireport.service.SAPAICore.claude.generated.model.CreateChatCompletionResponseChoicesInner.FinishReasonEnum.CONTENT_FILTER;
import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PACKAGE;

import com.google.common.annotations.Beta;
import customer.aireport.service.SAPAICore.claude.generated.model.CompletionUsage;
import customer.aireport.service.SAPAICore.claude.generated.model.CreateChatCompletionResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.CreateChatCompletionResponseChoicesInner;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;

/**
 * Represents the output of an OpenAI chat completion. *
 *
 * @since 1.4.0
 */
@Beta
@Value
@RequiredArgsConstructor(access = PACKAGE)
@Setter(value = NONE)
public class ClaudeAiChatCompletionResponse {
  /** The original response from the OpenAI API. */
  @Nonnull final CreateChatCompletionResponse originalResponse;

  /**
   * Gets the token usage from the original response.
   *
   * @return the token usage
   */
  @Nonnull
  public CompletionUsage getTokenUsage() {
    return getOriginalResponse().getUsage();
  }

  /**
   * Gets the first choice from the original response.
   *
   * @return the first choice
   */
  @Nonnull
  public CreateChatCompletionResponseChoicesInner getChoice() {
    return getOriginalResponse().getChoices().get(0);
  }

  /**
   * Gets the content of the first choice.
   *
   * <p>The content may be empty {@code ""} if the assistant did not return any content i.e. when
   * tool calls are present.
   *
   * @return the content of the first choice
   * @throws ClaudeAiClientException if the content is filtered by the content filter
   */
  @Nonnull
  public String getContent() {
    if (CONTENT_FILTER.equals(getOriginalResponse().getChoices().get(0).getFinishReason())) {
      throw new ClaudeAiClientException("Content filter filtered the output.");
    }

    return Objects.requireNonNullElse(getChoice().getMessage().getContent(), "");
  }

  /**
   * Gets the {@code ClaudeAiAssistantMessage} for the first choice.
   *
   * @return the assistant message
   * @throws ClaudeAiClientException if the content is filtered by the content filter
   * @since 1.6.0
   */
  @Nonnull
  public ClaudeAiAssistantMessage getMessage() {
    final var toolCalls = getChoice().getMessage().getToolCalls();

    if (toolCalls == null) {
      return ClaudeAiMessage.assistant(getContent());
    }

    final List<ClaudeAiContentItem> contentItems =
        getContent().isEmpty() ? List.of() : List.of(new ClaudeAiTextItem(getContent()));

    final var openAiToolCalls =
        toolCalls.stream()
            .<ClaudeAiToolCall>map(
                toolCall ->
                    new ClaudeAiFunctionCall(
                        toolCall.getId(),
                        toolCall.getFunction().getName(),
                        toolCall.getFunction().getArguments()))
            .toList();

    return new ClaudeAiAssistantMessage(new ClaudeAiMessageContent(contentItems), openAiToolCalls);
  }
}
