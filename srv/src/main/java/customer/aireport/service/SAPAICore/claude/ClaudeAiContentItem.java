package customer.aireport.service.SAPAICore.claude;

import com.google.common.annotations.Beta;

/**
 * Represents an item in a {@link ClaudeAiMessageContent} object.
 *
 * @since 1.4.0
 */
@Beta
public sealed interface ClaudeAiContentItem permits ClaudeAiTextItem, ClaudeAiImageItem {}
