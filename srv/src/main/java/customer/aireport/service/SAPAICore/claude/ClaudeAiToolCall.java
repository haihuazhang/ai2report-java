package customer.aireport.service.SAPAICore.claude;

import com.google.common.annotations.Beta;

/**
 * Represents a tool called by an OpenAI model.
 *
 * @since 1.6.0
 */
@Beta
public sealed interface ClaudeAiToolCall permits ClaudeAiFunctionCall {}
