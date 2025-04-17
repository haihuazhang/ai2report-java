package customer.aireport.factory;

import org.springframework.stereotype.Component;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseRequestUserMessage;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseRequestAssistantMessage;
import customer.aireport.service.SAPAICore.claude.generated.model.ContentBlock;
import customer.aireport.service.SAPAICore.claude.generated.model.SystemContentBlock;

@Component
public class SAPClaudeAIMessageFactory {
    
    public SystemContentBlock createSystemBlock(String content) {
        return new SystemContentBlock().text(content);
    }

    public ConverseRequestUserMessage createUserMessage(String content) {
        return new ConverseRequestUserMessage()
            .role(ConverseRequestUserMessage.RoleEnum.USER)
            .addContentItem(new ContentBlock().text(content));
    }

    public ConverseRequestAssistantMessage createAssistantMessage(String content) {
        return new ConverseRequestAssistantMessage()
            .role(ConverseRequestAssistantMessage.RoleEnum.ASSISTANT)
            .addContentItem(new ContentBlock().text(content));
    }
}