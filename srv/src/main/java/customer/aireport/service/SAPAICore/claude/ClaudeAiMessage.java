package customer.aireport.service.SAPAICore.claude;

import com.google.common.annotations.Beta;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Interface representing convenience wrappers of chat message to the openai service.
 *
 * @since 1.4.0
 */
@Beta
public sealed interface ClaudeAiMessage
    permits ClaudeAiUserMessage, ClaudeAiAssistantMessage, ClaudeAiToolMessage {
      // permits ClaudeAiUserMessage, ClaudeAiAssistantMessage, ClaudeAiSystemMessage, ClaudeAiToolMessage {

  /**
   * A convenience method to create a user message.
   *
   * @param message the message content.
   * @return the user message.
   */
  @Nonnull
  static ClaudeAiUserMessage user(@Nonnull final String message) {
    return new ClaudeAiUserMessage(message);
  }

  /**
   * A convenience method to create a user message containing only an image.
   *
   * @param openAiImageItem the message content.
   * @return the user message.
   */
  @Nonnull
  static ClaudeAiUserMessage user(@Nonnull final ClaudeAiImageItem openAiImageItem) {
    return new ClaudeAiUserMessage(new ClaudeAiMessageContent(List.of(openAiImageItem)));
  }

  /**
   * A convenience method to create an assistant message.
   *
   * @param message the message content.
   * @return the assistant message.
   */
  @Nonnull
  static ClaudeAiAssistantMessage assistant(@Nonnull final String message) {
    return new ClaudeAiAssistantMessage(message);
  }


  /**
   * A convenience method to create a tool message.
   *
   * @param message response of the executed tool call.
   * @param toolCallId identifier of the tool call that the assistant expected.
   * @return the tool message.
   */
  @Nonnull
  static ClaudeAiToolMessage tool(@Nonnull final String message, @Nonnull final String toolCallId) {
    return new ClaudeAiToolMessage(message, toolCallId);
  }

  /**
   * Returns the role associated with the message.
   *
   * @return the role.
   */
  @Nonnull
  String role();

  /**
   * Returns the content of the message.
   *
   * @return the content.
   */
  @Beta
  @Nonnull
  ClaudeAiMessageContent content();
}
