package customer.aireport.service.SAPAICore.claude;

import com.sap.ai.sdk.core.AiModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ClaudeAiModel(@Nonnull String name, @Nullable String version) implements AiModel {
    public static final ClaudeAiModel CLAUDE_3_5_SONNET = new ClaudeAiModel("anthropic--claude-3.5-sonnet", null);
}
