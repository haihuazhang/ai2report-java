package customer.aireport.service.SAPAICore.claude;

import static lombok.AccessLevel.PACKAGE;

import com.google.common.annotations.Beta;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestMessageContentPartImage;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestMessageContentPartImageImageUrl;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestMessageContentPartText;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestUserMessage;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestUserMessageContent;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionRequestUserMessageContentPart;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;

/**
 * Represents a chat message as 'user' to OpenAI service. *
 *
 * @since 1.4.0
 */
@Beta
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = PACKAGE)
public class ClaudeAiUserMessage implements ClaudeAiMessage {

  /** The role associated with this message. */
  @Nonnull String role = "user";

  /** The content of the message. */
  @Getter(onMethod_ = @Beta)
  @Nonnull
  ClaudeAiMessageContent content;

  /**
   * Creates a new user message from a string.
   *
   * @param message the first message.
   */
  @Tolerate
  ClaudeAiUserMessage(@Nonnull final String message) {
    this(new ClaudeAiMessageContent(List.of(new ClaudeAiTextItem(message))));
  }

  /**
   * Add text to the message.
   *
   * @param message the text to add.
   * @return the new message.
   */
  @Nonnull
  public ClaudeAiUserMessage withText(@Nonnull final String message) {
    final var contentItems = new LinkedList<>(content.items());
    contentItems.add(new ClaudeAiTextItem(message));
    return new ClaudeAiUserMessage(new ClaudeAiMessageContent(contentItems));
  }

  /**
   * Add an image to the message with the given image URL and detail level.
   *
   * @param imageUrl the URL of the image.
   * @param detailLevel the detail level of the image.
   * @return the new message.
   */
  @Nonnull
  public ClaudeAiUserMessage withImage(
      @Nonnull final String imageUrl, @Nonnull final ClaudeAiImageItem.DetailLevel detailLevel) {
    final var contentItems = new LinkedList<>(content.items());
    contentItems.add(new ClaudeAiImageItem(imageUrl, detailLevel));
    return new ClaudeAiUserMessage(new ClaudeAiMessageContent(contentItems));
  }

  /**
   * Add an image to the message with the given image URL.
   *
   * @param imageUrl the URL of the image.
   * @return the new message.
   */
  @Nonnull
  public ClaudeAiUserMessage withImage(@Nonnull final String imageUrl) {
    final var contentItems = new LinkedList<>(content.items());
    contentItems.add(new ClaudeAiImageItem(imageUrl));
    return new ClaudeAiUserMessage(new ClaudeAiMessageContent(contentItems));
  }

  /**
   * Converts the message to a serializable object.
   *
   * @return the corresponding {@code ChatCompletionRequestUserMessage} object.
   * @throws IllegalArgumentException if the content contains unsupported items.
   */
  @Nonnull
  ChatCompletionRequestUserMessage createChatCompletionRequestMessage()
      throws IllegalArgumentException {
    final var itemList = this.content().items();
    if (itemList.size() == 1 && itemList.get(0) instanceof ClaudeAiTextItem textItem) {
      return new ChatCompletionRequestUserMessage()
          .role(ChatCompletionRequestUserMessage.RoleEnum.fromValue(role()))
          .content(ChatCompletionRequestUserMessageContent.create(textItem.text()));
    }

    final var messageParts = new LinkedList<ChatCompletionRequestUserMessageContentPart>();
    for (final var item : itemList) {
      if (item instanceof ClaudeAiTextItem textItem) {
        messageParts.add(createTextContentPart(textItem));
      } else if (item instanceof ClaudeAiImageItem imageItem) {
        messageParts.add(createImageContentPart(imageItem));
      } else {
        throw new IllegalArgumentException(
            "Unknown content type for " + item.getClass() + " messages.");
      }
    }

    return new ChatCompletionRequestUserMessage()
        .role(ChatCompletionRequestUserMessage.RoleEnum.fromValue(role()))
        .content(ChatCompletionRequestUserMessageContent.create(messageParts));
  }

  @Nonnull
  private ChatCompletionRequestMessageContentPartText createTextContentPart(
      @Nonnull final ClaudeAiTextItem textItem) {
    return new ChatCompletionRequestMessageContentPartText()
        .type(ChatCompletionRequestMessageContentPartText.TypeEnum.TEXT)
        .text(textItem.text());
  }

  @Nonnull
  private ChatCompletionRequestMessageContentPartImage createImageContentPart(
      @Nonnull final ClaudeAiImageItem imageItem) {
    try {
      final var imageUrl =
          new ChatCompletionRequestMessageContentPartImageImageUrl()
              .url(new URI(imageItem.imageUrl()))
              .detail(
                  ChatCompletionRequestMessageContentPartImageImageUrl.DetailEnum.fromValue(
                      imageItem.detailLevel().toString()));

      return new ChatCompletionRequestMessageContentPartImage()
          .type(ChatCompletionRequestMessageContentPartImage.TypeEnum.IMAGE_URL)
          .imageUrl(imageUrl);

    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Provided image URL has invalid syntax.", e);
    }
  }
}
