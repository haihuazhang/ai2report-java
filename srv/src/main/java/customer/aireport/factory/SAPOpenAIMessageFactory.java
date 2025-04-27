package customer.aireport.factory;

import org.springframework.stereotype.Component;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage;

import customer.aireport.model.OpenAiChatAssistantMessage2;

@Component
public class SAPOpenAIMessageFactory {
    public OpenAiChatMessage[] createSystemMessage(String content) {
        return new OpenAiChatMessage[] {
            (new OpenAiChatMessage.OpenAiChatSystemMessage()).setContent(content)
        };
    }

    public OpenAiChatMessage[] createUserMessage(String content) {
        return new OpenAiChatMessage[] {
            (new OpenAiChatMessage.OpenAiChatUserMessage()).addText(content)
        };
    }

    public OpenAiChatMessage[] createAssistantMessage(String content) {
        // return 
        return new OpenAiChatMessage[] {
            (new OpenAiChatAssistantMessage2()).setContent(content)
        };
    }
}