package customer.aireport.service.AIService;

import java.util.List;

import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.Pcls;
import cds.gen.chatservice.Records;
import cds.gen.chatservice.RecordsAdoptContext;
import cds.gen.chatservice.ReportFields;
import cds.gen.chatservice.Reports;
import cds.gen.chatservice.ReportsGenerateCDSContext;
import cds.gen.chatservice.ReportsGeneratePCLContext;
import cds.gen.chatservice.ReportsNewRecordContext;
import customer.aireport.dto.CommonAIMessage;
import customer.aireport.model.EntityInfo;

// import org.checkerframework.checker.units.qual.mPERs;

public interface AIServiceI {
        public void callAICompletion(
                        List<CommonAIMessage> messages,
                        Reports report,
                        String userContent,
                        EntityInfo entityInfo,
                        ReportsNewRecordContext context);

        public List<ReportFields> callAIforAdopt(
                        ChatService readService,
                        RecordsAdoptContext context,
                        Records originalRecord,
                        Reports report);

        public List<Pcls> callAIforGeneratePCL(
                        String fieldsInJSON,
                        ChatService readService,
                        ReportsGeneratePCLContext generatePCLContext);

        public void callAIforGenerateCDS(
                        String fieldsInJSON,
                        ChatService readService,
                        ReportsGenerateCDSContext generateCDSContext,
                        Reports report);

}
