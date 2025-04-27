package customer.aireport.helper;

import org.springframework.stereotype.Component;
import cds.gen.chatservice.*;
import customer.aireport.constant.AIConstants;
import customer.aireport.dto.CommonAIMessage;
import customer.aireport.factory.RecordFactory;
import java.util.List;

@Component
public class ChatHelper {
    private final RecordFactory recordFactory;

    public ChatHelper(RecordFactory recordFactory) {
        this.recordFactory = recordFactory;
    }
    public Records handleNewChat(
            // OpenAiChatCompletionParameters params,
            List<CommonAIMessage> messages,
            String promptContent,
            String userContent,
            String reportId,
            Boolean isActiveEntity) {
        // params.addMessages(messageFactory.createSystemMessage(promptContent));
        // params.addMessages(messageFactory.createUserMessage(content));
        messages.add(new CommonAIMessage(AIConstants.Roles.SYSTEM, promptContent));
        messages.add(new CommonAIMessage(AIConstants.Roles.USER, userContent));

        return recordFactory.createSystemRecord(
            promptContent,
            reportId,
            isActiveEntity
        );
    }

    public void handleExistingChat(
        // OpenAiChatCompletionParameters params, 
        String userContent,
        List<CommonAIMessage> messages,
        List<Records> records) {
        records.forEach(record -> {
            // OpenAiChatMessage[] message = switch (record.getRole()) {
            //     case AIConstants.Roles.SYSTEM -> messageFactory.createSystemMessage(record.getContent());
            //     case AIConstants.Roles.USER -> messageFactory.createUserMessage(record.getContent());
            //     case AIConstants.Roles.ASSISTANT -> messageFactory.createAssistantMessage(record.getContent());
            //     default -> throw new BusinessException(AIConstants.Messages.UNEXPECTED_ROLE + record.getRole());
            // };
            // params.addMessages(message);
            messages.add(new CommonAIMessage(record.getRole(), record.getContent()));
        });
        messages.add(new CommonAIMessage(AIConstants.Roles.USER, userContent));
    }
}