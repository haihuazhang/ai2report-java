package customer.aireport.service.SAPAICore.claude;

import static com.sap.ai.sdk.core.JacksonConfiguration.getDefaultObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestMessage;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionsCreate200Response;
import javax.annotation.Nonnull;

/**
 * Utility class for handling OpenAI module.
 *
 * <p><b>Only intended for internal usage within this SDK</b>.
 *
 * @since 1.4.0
 */
@Beta
class ClaudeAiUtils {

  /**
   * Converts an ClaudeAiMessage to a ChatCompletionRequestMessage.
   *
   * @param message the ClaudeAiMessage to convert
   * @return the corresponding ChatCompletionRequestMessage
   * @throws IllegalArgumentException if the message type is unknown
   */
  @Nonnull
  static ChatCompletionRequestMessage createChatCompletionRequestMessage(
      @Nonnull final ClaudeAiMessage message) throws IllegalArgumentException {
    if (message instanceof ClaudeAiUserMessage userMessage) {
      return userMessage.createChatCompletionRequestMessage();
    } else if (message instanceof ClaudeAiAssistantMessage assistantMessage) {
      return assistantMessage.createChatCompletionRequestMessage();
    // } else if (message instanceof ClaudeAiSystemMessage systemMessage) {
    //   return systemMessage.createChatCompletionRequestMessage();
    } else if (message instanceof ClaudeAiToolMessage toolMessage) {
      return toolMessage.createChatCompletionRequestMessage();
    } else {
      throw new IllegalArgumentException("Unknown message type: " + message.getClass());
    }
  }

  /**
   * Default object mapper used for JSON de-/serialization.
   *
   * @return A new object mapper with the default configuration.
   */
  @Nonnull
  static ObjectMapper getClaudeAiObjectMapper() {
    return getDefaultObjectMapper()
        .addMixIn(
            ChatCompletionsCreate200Response.class,
            JacksonMixins.DefaultChatCompletionCreate200ResponseMixIn.class);
  }
}
