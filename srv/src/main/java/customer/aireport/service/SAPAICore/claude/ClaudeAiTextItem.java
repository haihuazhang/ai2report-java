package customer.aireport.service.SAPAICore.claude;

import com.google.common.annotations.Beta;
import javax.annotation.Nonnull;

/**
 * Represents a text item in a {@link ClaudeAiMessageContent} object.
 *
 * @param text the text of the item
 * @since 1.4.0
 */
@Beta
public record ClaudeAiTextItem(@Nonnull String text) implements ClaudeAiContentItem {}
