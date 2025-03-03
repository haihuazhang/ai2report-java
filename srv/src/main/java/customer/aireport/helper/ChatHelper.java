package customer.aireport.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sap.ai.sdk.foundationmodels.openai.model.*;
import cds.gen.chatservice.*;
import customer.aireport.constant.AIConstants;
import customer.aireport.exception.BusinessException;
import customer.aireport.factory.MessageFactory;
import customer.aireport.factory.RecordFactory;
import java.util.List;

@Component
public class ChatHelper {
    
    @Autowired
    private MessageFactory messageFactory;
    
    @Autowired
    private RecordFactory recordFactory;

    public Records handleNewChat(
            OpenAiChatCompletionParameters params,
            String promptContent,
            String content,
            String reportId,
            Boolean isActiveEntity) {
        params.addMessages(messageFactory.createSystemMessage(promptContent));
        params.addMessages(messageFactory.createUserMessage(content));

        return recordFactory.createSystemRecord(
            promptContent,
            reportId,
            isActiveEntity
        );
    }

    public void handleExistingChat(OpenAiChatCompletionParameters params, List<Records> records) {
        records.forEach(record -> {
            OpenAiChatMessage[] message = switch (record.getRole()) {
                case AIConstants.Roles.SYSTEM -> messageFactory.createSystemMessage(record.getContent());
                case AIConstants.Roles.USER -> messageFactory.createUserMessage(record.getContent());
                case AIConstants.Roles.ASSISTANT -> messageFactory.createAssistantMessage(record.getContent());
                default -> throw new BusinessException(AIConstants.Messages.UNEXPECTED_ROLE + record.getRole());
            };
            params.addMessages(message);
        });
    }
}