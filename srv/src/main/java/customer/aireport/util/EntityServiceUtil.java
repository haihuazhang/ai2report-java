package customer.aireport.util;

import com.sap.cds.CdsData;
import com.sap.cds.Result;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnSelect;

import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.ParameterItems;
import cds.gen.chatservice.ParameterItems_;
import cds.gen.chatservice.Records;
import cds.gen.chatservice.Records_;
import cds.gen.chatservice.ReportFields;
import cds.gen.chatservice.ReportFields_;
import cds.gen.chatservice.Reports;
import customer.aireport.exception.AIServiceException;
import cds.gen.chatservice.Pcls;
import cds.gen.chatservice.Pcls_;

import java.util.List;

public class EntityServiceUtil {
    
    // Select operations
    public static <T> T selectSingle(ChatService service, CqnSelect select, Class<T> type, String errorMessage) {
        Result result = service.run(select);
        if (result.rowCount() == 0) {
            throw AIServiceException.notFound(errorMessage);
        }
        return result.single(type);
    }

    public static <T> List<T> selectList(ChatService service, CqnSelect select, Class<T> type) {
        Result result = service.run(select);
        return result.listOf(type);
    }

    public static Records selectRecordById(ChatService service, String reportId) {
        CqnSelect select = Select.from(Records_.class)
            .where(b -> b.report_ID().eq(reportId))
            .orderBy(c -> c.createdAt().asc());
        return service.run(select).single(Records.class);
    }

    public static List<Records> selectRecordsByReportId(ChatService service, String reportId) {
        CqnSelect select = Select.from(Records_.class)
            .where(b -> b.report_ID().eq(reportId))
            .orderBy(c -> c.createdAt().asc());
        return selectList(service, select, Records.class);
    }

    public static List<ReportFields> selectReportFieldsByReportId(ChatService service, String reportId) {
        CqnSelect select = Select.from(ReportFields_.class)
            .where(b -> b.report_ID().eq(reportId));
        return selectList(service, select, ReportFields.class);
    }

    public static ParameterItems selectParameterItem(ChatService service, String name, String language, String errorMessage) {
        CqnSelect select = Select.from(ParameterItems_.class)
            .where(b -> b.name().eq(name).and(b.language().eq(language)));
        return selectSingle(service, select, ParameterItems.class, errorMessage);
    }

    // Insert operations
    public static Result insertRecord(ChatService service, ChatService.Draft serviceDraft, 
            Records record, Boolean isActiveEntity) {
        if (isActiveEntity) {
            return service.run(Insert.into(Records_.class).entry(record));
        } else {
            return serviceDraft.newDraft(Insert.into(Records_.class).entry(record));
        }
    }

    public static Result insertReportField(ChatService service, ChatService.Draft serviceDraft, 
            ReportFields field, Boolean isActiveEntity) {
        if (isActiveEntity) {
            return service.run(Insert.into(ReportFields_.class).entry(field));
        } else {
            return serviceDraft.newDraft(Insert.into(ReportFields_.class).entry(field));
        }
    }

    public static Result insertPcl(ChatService service, ChatService.Draft serviceDraft, 
            Pcls pcl, Boolean isActiveEntity) {
        if (isActiveEntity) {
            return service.run(Insert.into(Pcls_.class).entry(pcl));
        } else {
            return serviceDraft.newDraft(Insert.into(Pcls_.class).entry(pcl));
        }
    }

    public static void insertReportFields(ChatService service, ChatService.Draft serviceDraft, 
            List<ReportFields> fieldsList, Reports report) {
        fieldsList.forEach(field -> {
            field.setReportId(report.getId());
            field.setIsActiveEntity(report.getIsActiveEntity());
            insertReportField(service, serviceDraft, field, report.getIsActiveEntity());
        });
    }

    public static void updateRecordStatus(ChatService service, ChatService.Draft serviceDraft, Records record) {
        record.setIsAdopted(true);
        if (record.getIsActiveEntity()) {
            service.run(Update.entity(Records_.class).data(record));
        } else {
            serviceDraft.patchDraft(Update.entity(Records_.class).data(record));
        }
    }

    // Delete operations
    public static void deleteReportFieldsByReportId(ChatService service, String reportId) {
        CqnDelete delete = Delete.from(ReportFields_.class)
            .where(b -> b.report_ID().eq(reportId));
        service.run(delete);
    }

    public static void deletePclsByReportId(ChatService service, String reportId) {
        CqnDelete delete = Delete.from(Pcls_.class)
            .where(b -> b.report_ID().eq(reportId));
        service.run(delete);
    }

    public static <T extends CdsData> void batchInsert(
            ChatService service, 
            ChatService.Draft serviceDraft,
            List<T> entities, 
            String reportId, 
            Boolean isActiveEntity) {
        entities.forEach(entity -> {
            // Use reflection to set common properties
            try {
                entity.getClass().getMethod("setReportId", String.class)
                    .invoke(entity, reportId);
                entity.getClass().getMethod("setIsActiveEntity", Boolean.class)
                    .invoke(entity, isActiveEntity);
            } catch (Exception e) {
                throw new AIServiceException("Failed_To_Set_Entity_Properties", e);
            }
            insertEntity(service, serviceDraft, entity, isActiveEntity);
        });
    }

    private static <T extends CdsData> void insertEntity(
            ChatService service, 
            ChatService.Draft serviceDraft,
            T entity, 
            Boolean isActiveEntity) {
        if (entity instanceof Records) {
            insertRecord(service, serviceDraft, (Records) entity, isActiveEntity);
        } else if (entity instanceof Pcls) {
            insertPcl(service, serviceDraft, (Pcls) entity, isActiveEntity);
        } else if (entity instanceof ReportFields) {
            insertReportField(service, serviceDraft, (ReportFields) entity, isActiveEntity);
        }
    }
}