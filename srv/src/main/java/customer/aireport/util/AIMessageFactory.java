package customer.aireport.util;

import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage;

public class AIMessageFactory {
    public static OpenAiChatMessage[] createSystemMessage(String content) {
        return new OpenAiChatMessage[] {
            (new OpenAiChatMessage.OpenAiChatSystemMessage()).setContent(content)
        };
    }

    public static OpenAiChatMessage[] createUserMessage(String content) {
        return new OpenAiChatMessage[] {
            (new OpenAiChatMessage.OpenAiChatUserMessage()).addText(content)
        };
    }

    public static OpenAiChatMessage[] createAssistantMessage(String content) {
        return new OpenAiChatMessage[] {
            (new OpenAiChatAssistantMessage2()).setContent(content)
        };
    }
}