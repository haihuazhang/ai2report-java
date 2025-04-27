package customer.aireport.service.AIService;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
import customer.aireport.model.StreamChatRequest;

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

        public SseEmitter callAIforStream(
                        List<CommonAIMessage> messages,
                        Reports report,
                        StreamChatRequest request);

        /**
         * Send a chunk to the emitter
         *
         * @param emitter The emitter to send the chunk to
         * @param chunk   The chunk to send
         */
        public static void send(@Nonnull final SseEmitter emitter, @Nonnull final String chunk) {
                try {
                        emitter.send(chunk);
                } catch (final IOException e) {
                        emitter.completeWithError(e);
                }
        }
}
