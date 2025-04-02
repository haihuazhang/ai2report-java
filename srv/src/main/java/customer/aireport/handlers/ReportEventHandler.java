package customer.aireport.handlers;

// Generic imports
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionFunction;
// // SAP imports
// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionOutput;
// import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionParameters;
import com.sap.cds.Result;
// import com.sap.cds.ql.Select;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

// Application imports
import customer.aireport.config.AIProperties;
import customer.aireport.constant.AIConstants;
import customer.aireport.dto.CommonAIMessage;
import customer.aireport.factory.RecordFactory;
import customer.aireport.helper.ChatHelper;
import customer.aireport.model.EntityInfo;
import customer.aireport.service.AIServiceI;
import customer.aireport.service.EntityService;
import customer.aireport.util.ConfigUtils;
import customer.aireport.util.JsonUtils;
import customer.aireport.util.RequestUtils;

// Generated imports
import cds.gen.chatservice.*;

/**
 * Event handler for chat service operations.
 * Handles various operations like new messages, record adoption, and PCL
 * generation.
 */
@Component
@ServiceName(value = ChatService_.CDS_NAME)
public class ReportEventHandler implements EventHandler {
        // Service dependencies
        @Autowired
        @Qualifier("sapAIService")
        private AIServiceI sapAIService;

        // @Autowired
        // @Qualifier("directOpenAIService")
        // private AIServiceI directOpenAIService;

        // @Autowired
        // @Qualifier("deepSeekService")
        // private AIServiceI deepSeekService;

        // private AIService getActiveAIService() {
        // return switch (configUtils.getAIServiceType()) {
        // case SAP -> sapAIService;
        // case DIRECT -> directOpenAIService;
        // case DEEPSEEK -> deepSeekService;
        // };
        // }

        @Autowired
        private ChatService aiService;

        @Autowired
        private EntityService entityService; // 保持这个注入

        @Autowired
        private AIProperties aiProperties;

        @Autowired
        private ChatService.Draft aiServiceDraft;

        @Autowired
        private ConfigUtils configUtils; // Add ConfigUtils injection

        // @Autowired
        // private AIResponseHelper aiResponseHelper;

        @Autowired
        private ChatHelper chatHelper;

        @Autowired
        private RecordFactory recordFactory;

        @Autowired
        private RequestUtils requestUtils;

        @Autowired
        private JsonUtils jsonUtils;

        /**
         * Handles new message events in the chat service.
         * Creates a new chat record or appends to existing conversation.
         * 
         * @param context The new record context containing message details
         */
        @On(event = ReportsNewRecordContext.CDS_NAME, entity = Reports_.CDS_NAME)
        public void newMessage(ReportsNewRecordContext context) {
                // Get entity info from request
                EntityInfo entityInfo = requestUtils.analyzeRequest(context.getCqn(), context.getModel());

                // Retrieve existing records and report
                List<Records> records = entityService.selectRecordsByReportId(aiService, entityInfo.getId());
                Reports report = entityService.selectSingle(
                                aiService,
                                context.getCqn(),
                                Reports.class,
                                AIConstants.Messages.REPORT_NOT_FOUND);

                // Get system prompt content
                String promptContent = configUtils.getPrompt(
                                aiService,
                                context.getParameterInfo().getLocale(),
                                aiProperties.getPromptPrefixForReport());

                // Prepare chat parameters
                // OpenAiChatCompletionParameters aiChatCompletionParameters = new
                // OpenAiChatCompletionParameters();
                List<CommonAIMessage> commonAIMessages = new ArrayList<>();

                // Handle new or existing chat
                if (records.isEmpty()) {
                        // Create new system record for first-time chat
                        Records systemRecord = chatHelper.handleNewChat(
                                        commonAIMessages,
                                        promptContent,
                                        context.getContent(),
                                        entityInfo.getId(),
                                        entityInfo.getIsActiveEntity());
                        entityService.insertRecord(aiService, aiServiceDraft, systemRecord,
                                        entityInfo.getIsActiveEntity());
                } else {
                        // Add existing chat history
                        chatHelper.handleExistingChat(commonAIMessages, records);
                }

                // Get AI response and handle it
                sapAIService.callAICompletion(commonAIMessages,
                                report,
                                context.getContent(),
                                entityInfo,
                                context);

        }

        /**
         * Handles record adoption events.
         * Processes the record content and extracts report fields.
         * 
         * @param adoptContext The adoption context containing record details
         */
        @On(event = RecordsAdoptContext.CDS_NAME, entity = Records_.CDS_NAME)
        public void adopt(RecordsAdoptContext adoptContext) {

                // Get record to be adopted
                Records record = entityService.selectSingle(
                                aiService,
                                adoptContext.getCqn(),
                                Records.class,
                                AIConstants.Messages.RECORD_NOT_FOUND);

                // Get associated report using the new method
                Reports report = entityService.selectReportById(
                                aiService,
                                record.getReportId(),
                                record.getIsActiveEntity(),
                                AIConstants.Messages.REPORT_NOT_FOUND);

                // Process fields and update database
                entityService.deleteReportFieldsByReportId(aiService, record.getReportId());

                List<ReportFields> fieldsList = sapAIService.callAIforAdopt(
                                aiService,
                                adoptContext,
                                record,
                                report);

                // // Get AI function for JSON processing
                // OpenAiChatCompletionFunction function = configUtils.getFunction(
                // aiService,
                // adoptContext.getParameterInfo().getLocale(),
                // aiProperties.getFunctionForJson());

                // // Call AI to process record content
                // OpenAiChatCompletionOutput aiResult = sapAIService.callAIWithFunction(
                // function,
                // record.getContent(),
                // "");

                // List<ReportFields> fieldsList = new ArrayList<>();
                // aiResponseHelper.processResponse(aiResult, report, fieldsList,
                // AIConstants.NodeKeys.FIELDS,
                // jsonUtils::createReportField);

                // Insert new fields and update record status
                entityService.batchInsert(aiService, aiServiceDraft, fieldsList, record.getReportId(),
                                record.getIsActiveEntity());
                entityService.updateRecordStatus(aiService, aiServiceDraft, record);

                adoptContext.setResult(record);
        }

        /**
         * Handles append to chat record events.
         * Converts report fields to JSON and creates a new chat record.
         * 
         * @param context The append context containing record details
         */
        @On(event = ReportsAppendToChatRecordContext.CDS_NAME, entity = Reports_.CDS_NAME)
        public void appendToChatRecord(ReportsAppendToChatRecordContext context) {
                EntityInfo entityInfo = requestUtils.analyzeRequest(context.getCqn(), context.getModel());

                List<ReportFields> fields = entityService.selectReportFieldsByReportId(
                                entityInfo.getIsActiveEntity() ? aiService : aiServiceDraft,
                                entityInfo.getId());

                Records newRecord = recordFactory.createUserRecord(
                                jsonUtils.convertFieldsToJson(fields),
                                entityInfo.getId(),
                                entityInfo.getIsActiveEntity());

                Result result = entityService.insertRecord(aiService, aiServiceDraft, newRecord,
                                entityInfo.getIsActiveEntity());
                context.setResult(result.single(Records.class));
        }

        /**
         * Generates PCL (Process Control List) from report fields.
         * Processes fields through AI and creates PCL records.
         * 
         * @param generatePCLContext The PCL generation context
         */
        @On(event = ReportsGeneratePCLContext.CDS_NAME, entity = Reports_.CDS_NAME)
        public void generatePCL(ReportsGeneratePCLContext generatePCLContext) {
                EntityInfo entityInfo = requestUtils.analyzeRequest(
                                generatePCLContext.getCqn(),
                                generatePCLContext.getModel());

                // Get fields and convert to JSON
                List<ReportFields> reportFields = entityService.selectReportFieldsByReportId(
                                aiService,
                                entityInfo.getId());
                String fieldsJson = jsonUtils.convertFieldsToJson(reportFields);

                List<Pcls> pclsList = sapAIService.callAIforGeneratePCL(fieldsJson, aiService, generatePCLContext);
                // // Get parameters
                // AIParameters params = configUtils.getFunctionAndPrompt(
                // aiService,
                // generatePCLContext.getParameterInfo().getLocale(),
                // aiProperties.getFunctionForPcl(),
                // aiProperties.getPromptPrefixForPcl());

                // // Call AI and process response
                // OpenAiChatCompletionOutput aiResult = sapAIService.callAIWithFunction(
                // params.getFunction(),
                // fieldsJson,
                // params.getPromptContent());

                // // Process PCLs
                // List<Pcls> pclsList = new ArrayList<>();
                // aiResponseHelper.processResponse(
                // aiResult,
                // null,
                // pclsList,
                // "pcl", // Changed from AIConstants.NodeKeys.ITEMS to match function schema
                // jsonUtils::createPcl);

                // Delete old and insert new PCLs
                entityService.deletePclsByReportId(aiService, entityInfo.getId());
                entityService.batchInsert(
                                aiService,
                                aiServiceDraft,
                                pclsList,
                                entityInfo.getId(),
                                entityInfo.getIsActiveEntity());

                generatePCLContext.setCompleted();
        }

        /**
         * Generates CDS (Core Data Services) code from report fields.
         * Processes fields through AI to create CDS entity definitions.
         * 
         * @param generateCDSContext The CDS generation context
         */
        @On(event = ReportsGenerateCDSContext.CDS_NAME, entity = Reports_.CDS_NAME)
        public void generateCDS(ReportsGenerateCDSContext generateCDSContext) {
                // Get entity info from request
                EntityInfo entityInfo = requestUtils.analyzeRequest(
                                generateCDSContext.getCqn(),
                                generateCDSContext.getModel());

                // Get report
                Reports report = entityService.selectSingle(
                                aiService,
                                generateCDSContext.getCqn(),
                                Reports.class,
                                AIConstants.Messages.REPORT_NOT_FOUND);

                // Get fields and convert to JSON
                List<ReportFields> reportFields = entityService.selectReportFieldsByReportId(
                                aiService,
                                entityInfo.getId());
                String fieldsJson = jsonUtils.convertFieldsToJson(reportFields);

                sapAIService.callAIforGenerateCDS(fieldsJson, aiService, generateCDSContext, report);

                // // Get AI parameters
                // AIParameters params = configUtils.getFunctionAndPrompt(
                // aiService,
                // generateCDSContext.getParameterInfo().getLocale(),
                // aiProperties.getFunctionForCds(),
                // aiProperties.getPromptPrefixForCds());

                // // Call AI service
                // OpenAiChatCompletionOutput aiResult = sapAIService.callAIWithFunction(
                // params.getFunction(),
                // fieldsJson,
                // params.getPromptContent());

                // // Process response and update CDS
                // aiResponseHelper.handleCDSResponse(aiResult, report);

                // generateCDSContext.setResult(report);
                generateCDSContext.setCompleted();
        }
}
