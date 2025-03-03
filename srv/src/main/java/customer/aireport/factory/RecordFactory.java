package customer.aireport.factory;

import org.springframework.stereotype.Component;
import java.util.UUID;
import cds.gen.chatservice.Records;
import customer.aireport.constant.AIConstants;

@Component
public class RecordFactory {
    public Records createRecord(String role, String content, String reportId, Boolean isActiveEntity) {
        Records record = Records.create();
        record.setId(UUID.randomUUID().toString());
        record.setRole(role);
        record.setContent(content);
        record.setIsAdopted(false);
        record.setReportId(reportId);
        record.setIsActiveEntity(isActiveEntity);
        return record;
    }

    public Records createSystemRecord(String content, String reportId, Boolean isActiveEntity) {
        return createRecord(AIConstants.Roles.SYSTEM, content, reportId, isActiveEntity);
    }

    public Records createUserRecord(String content, String reportId, Boolean isActiveEntity) {
        return createRecord(AIConstants.Roles.USER, content, reportId, isActiveEntity);
    }

    public Records createAssistantRecord(String content, String reportId, Boolean isActiveEntity) {
        Records record = createRecord(AIConstants.Roles.ASSISTANT, content, reportId, isActiveEntity);
        record.setCreatedBy("AI");
        return record;
    }
}