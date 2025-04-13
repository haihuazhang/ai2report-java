package customer.aireport.service.SAPAICore.claude;

import static lombok.AccessLevel.PACKAGE;

import com.google.common.annotations.Beta;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionMessageToolCall;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionMessageToolCallFunction;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestAssistantMessage;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestAssistantMessageContent;
import customer.aireport.service.SAPAICore.claude.generated.model.ToolCallType;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Represents a chat message as 'assistant' to OpenAI service.
 *
 * <p>When {@link ClaudeAiAssistantMessage} is received from {@link ClaudeAiChatCompletionResponse}, it
 * may contain tool calls that need to be executed. The tool calls are represented as {@link
 * ClaudeAiToolCall}.
 *
 * @since 1.4.0
 */
@Beta
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = PACKAGE)
public class ClaudeAiAssistantMessage implements ClaudeAiMessage {

  /** The role associated with this message. */
  @Nonnull String role = "assistant";

  /**
   * The content of the message.
   *
   * <p>May contain an empty list of {@link ClaudeAiContentItem} when tool calls are present.
   */
  @Getter(onMethod_ = @Beta)
  @Nonnull
  ClaudeAiMessageContent content;

  /**
   * The tool calls associated with this message if present.
   *
   * @since 1.6.0
   */
  @Getter(onMethod_ = @Beta)
  @Nonnull
  List<ClaudeAiToolCall> toolCalls;

  /**
   * Creates a new assistant message with the given single message as text content.
   *
   * @param singleMessage the message.
   */
  ClaudeAiAssistantMessage(@Nonnull final String singleMessage) {
    this(
        new ClaudeAiMessageContent(List.of(new ClaudeAiTextItem(singleMessage))),
        Collections.emptyList());
  }

  /**
   * Converts the message to a serializable object.
   *
   * @return the corresponding {@code ChatCompletionRequestAssistantMessage} object.
   */
  @Nonnull
  ChatCompletionRequestAssistantMessage createChatCompletionRequestMessage() {
    final var message =
        new ChatCompletionRequestAssistantMessage()
            .role(ChatCompletionRequestAssistantMessage.RoleEnum.fromValue(role()));

    final var items = content().items();
    if (!items.isEmpty() && items.get(0) instanceof ClaudeAiTextItem textItem) {
      message.content(ChatCompletionRequestAssistantMessageContent.create(textItem.text()));
    }

    for (final var item : toolCalls()) {
      if (item instanceof ClaudeAiFunctionCall functionItem) {
        final var functionCall =
            new ChatCompletionMessageToolCallFunction()
                .name(functionItem.getName())
                .arguments(functionItem.getArguments());

        final var toolCall =
            new ChatCompletionMessageToolCall()
                .type(ToolCallType.FUNCTION)
                .id(functionItem.getId())
                .function(functionCall);

        message.addToolCallsItem(toolCall);
      }
    }
    return message;
  }
}
