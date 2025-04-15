package customer.aireport.factory;

import org.springframework.stereotype.Component;
import customer.aireport.service.SAPAICore.claude.generated.model.RequestUserMessage;
import customer.aireport.service.SAPAICore.claude.generated.model.RequestUserMessageContent;
import customer.aireport.service.SAPAICore.claude.generated.model.RequestAssistantMessage;

@Component
public class SAPClaudeAIMessageFactory {
    // public RequestUserMessage[] createSystemMessage(String content) {
    //     return new OpenAiChatMessage[] {
    //         (new OpenAiChatMessage.OpenAiChatSystemMessage()).setContent(content)
    //     };
    // }

    public RequestUserMessage createUserMessage(String content) {
        RequestUserMessageContent userMessageContent = new RequestUserMessageContent.InnerString(content);
        RequestUserMessage userMessage = new RequestUserMessage();
        userMessage.setRole(RequestUserMessage.RoleEnum.USER);
        userMessage.setContent(userMessageContent);
        return userMessage ;
    }

    public RequestAssistantMessage createAssistantMessage(String content) {
        RequestUserMessageContent messageContent = new RequestUserMessageContent.InnerString(content);
        RequestAssistantMessage assistantMessage = new RequestAssistantMessage();
        assistantMessage.setRole(RequestAssistantMessage.RoleEnum.ASSISTANT);
        assistantMessage.setContent(messageContent);
        return assistantMessage;
    }
}