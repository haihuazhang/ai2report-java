package customer.aireport.util;

import java.util.UUID;
import cds.gen.chatservice.Records;

public class RecordFactory {
    public static Records createRecord(String role, String content, String reportId, Boolean isActiveEntity) {
        Records record = Records.create();
        record.setId(UUID.randomUUID().toString());
        record.setRole(role);
        record.setContent(content);
        record.setIsAdopted(false);
        record.setReportId(reportId);
        record.setIsActiveEntity(isActiveEntity);
        return record;
    }

    public static Records createSystemRecord(String content, String reportId, Boolean isActiveEntity) {
        return createRecord("system", content, reportId, isActiveEntity);
    }

    public static Records createUserRecord(String content, String reportId, Boolean isActiveEntity) {
        return createRecord("user", content, reportId, isActiveEntity);
    }

    public static Records createAssistantRecord(String content, String reportId, Boolean isActiveEntity) {
        Records record = createRecord("assistant", content, reportId, isActiveEntity);
        record.setCreatedBy("AI");
        return record;
    }
}