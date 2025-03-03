package customer.aireport.handlers;

// Generic imports
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionFunction;
// SAP imports
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionOutput;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionParameters;
import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

// Application imports
import customer.aireport.config.AIProperties;
import customer.aireport.constant.AIConstants;
import customer.aireport.factory.RecordFactory;
import customer.aireport.helper.AIResponseHelper;
import customer.aireport.helper.ChatHelper;
import customer.aireport.model.AIParameters;
import customer.aireport.model.EntityInfo;
import customer.aireport.service.AIService;
import customer.aireport.service.EntityService;
import customer.aireport.util.ConfigUtils;
import customer.aireport.util.JsonUtils;
import customer.aireport.util.RequestUtils;

// Generated imports
import cds.gen.chatservice.*;

@Component
@ServiceName(value = ChatService_.CDS_NAME)
public class ReportEventHandler implements EventHandler {

        @Autowired
        @Qualifier("openAIService") // Add qualifier for the AI service
        private AIService openAIService; // Renamed from aiService to openAIService

        @Autowired
        private ChatService aiService; // Keep original ChatService

        @Autowired
        private EntityService entityService; // 保持这个注入

        @Autowired
        private AIProperties aiProperties;

        @Autowired
        private ChatService.Draft aiServiceDraft;

        @Autowired
        private ConfigUtils configUtils; // Add ConfigUtils injection

        @Autowired
        private AIResponseHelper aiResponseHelper;

        @Autowired
        private ChatHelper chatHelper;

        @Autowired
        private RecordFactory recordFactory;

        @Autowired
        private RequestUtils requestUtils;

        @Autowired
        private JsonUtils jsonUtils;

        @On(event = ReportsNewRecordContext.CDS_NAME, entity = Reports_.CDS_NAME)
        public void newMessage(ReportsNewRecordContext context) {
                EntityInfo entityInfo = requestUtils.analyzeRequest(context.getCqn(), context.getModel());

                List<Records> records = entityService.selectRecordsByReportId(aiService, entityInfo.getId());
                Reports report = entityService.selectSingle(
                                aiService,
                                context.getCqn(),
                                Reports.class,
                                AIConstants.Messages.REPORT_NOT_FOUND);

                String localString = requestUtils.getLocaleString(context.getParameterInfo().getLocale());
                // 只需要 prompt
                String promptContent = configUtils.getPrompt(
                                aiService,
                                localString,
                                aiProperties.getPromptPrefixForReport());

                OpenAiChatCompletionParameters aiChatCompletionParameters = new OpenAiChatCompletionParameters();

                if (records.isEmpty()) {
                        Records systemRecord = chatHelper.handleNewChat(
                                        aiChatCompletionParameters,
                                        promptContent,
                                        context.getContent(),
                                        entityInfo.getId(),
                                        entityInfo.getIsActiveEntity());
                        entityService.insertRecord(aiService, aiServiceDraft, systemRecord,
                                        entityInfo.getIsActiveEntity());
                } else {
                        chatHelper.handleExistingChat(aiChatCompletionParameters, records);
                }

                OpenAiChatCompletionOutput aiResult = openAIService.callAICompletion(aiChatCompletionParameters);
                aiResponseHelper.handleChatResponse(
                                aiResult,
                                report,
                                context.getContent(),
                                entityInfo,
                                // report.getId(),
                                // entityInfo.getIsActiveEntity(),
                                context);
        }

        // private void handleAIResponse(OpenAiChatCompletionOutput aiResult, Reports
        // report,
        // ReportsNewRecordContext context, Boolean isActiveEntity) {
        // Records userRecord = recordFactory.createUserRecord(context.getContent(),
        // report.getId(),
        // isActiveEntity);
        // Records assistRecord =
        // recordFactory.createAssistantRecord(aiResult.getContent(), report.getId(),
        // isActiveEntity);

        // entityService.insertRecord(aiService, aiServiceDraft, userRecord,
        // isActiveEntity);

        // try {
        // Thread.sleep(AIConstants.CHAT_DELAY_MS);
        // } catch (InterruptedException e) {
        // Thread.currentThread().interrupt();
        // throw new BusinessException(AIConstants.Messages.THREAD_INTERRUPTED, e);
        // }

        // Result assistResult = entityService.insertRecord(aiService, aiServiceDraft,
        // assistRecord,
        // isActiveEntity);
        // context.setResult(assistResult.single(Records.class));
        // }

        // private <T> void processAIResponse(OpenAiChatCompletionOutput aiResult,
        // Reports report,
        // List<T> resultList, String nodeKey, AIResponseProcessor<T> processor) {
        // aiResponseHelper.processResponse(aiResult, report, resultList, nodeKey,
        // processor);
        // }

        @On(event = RecordsAdoptContext.CDS_NAME, entity = Records_.CDS_NAME)
        public void adopt(RecordsAdoptContext adoptContext) {
                Records record = entityService.selectSingle(
                                aiService,
                                adoptContext.getCqn(),
                                Records.class,
                                AIConstants.Messages.RECORD_NOT_FOUND);

                String localString = requestUtils.getLocaleString(adoptContext.getParameterInfo().getLocale());
                // 需要 function 和 prompt
                // AIParameters params = configUtils.getFunctionAndPrompt(
                // aiService,
                // aiProperties,
                // localString,
                // aiProperties.getFunctionForJson(),
                // aiProperties.getPromptPrefixForJson());
                OpenAiChatCompletionFunction function = configUtils.getFunction(aiService, localString,
                                aiProperties.getFunctionForJson());

                OpenAiChatCompletionOutput aiResult = openAIService.callAIWithFunction(
                                function,
                                record.getContent(),
                                "");

                Reports report = entityService.selectSingle(
                                aiService,
                                Select.from(Reports_.class)
                                                .where(b -> b.ID().eq(record.getReportId())
                                                                .and(b.IsActiveEntity()
                                                                                .eq(record.getIsActiveEntity()))),
                                Reports.class,
                                AIConstants.Messages.REPORT_NOT_FOUND);

                List<ReportFields> fieldsList = new ArrayList<>();
                entityService.deleteReportFieldsByReportId(aiService, record.getReportId());
                aiResponseHelper.processResponse(aiResult, report, fieldsList, AIConstants.NodeKeys.FIELDS,
                                jsonUtils::createReportField);

                entityService.batchInsert(
                                aiService,
                                aiServiceDraft,
                                fieldsList,
                                record.getReportId(),
                                record.getIsActiveEntity());
                entityService.updateRecordStatus(aiService, aiServiceDraft, record);

                adoptContext.setResult(record);
        }

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

        @On(event = ReportsGeneratePCLContext.CDS_NAME, entity = Reports_.CDS_NAME)
        public void generatePCL(ReportsGeneratePCLContext generatePCLContext) {
                EntityInfo entityInfo = requestUtils.analyzeRequest(
                                generatePCLContext.getCqn(),
                                generatePCLContext.getModel());

                String localString = requestUtils.getLocaleString(generatePCLContext.getParameterInfo().getLocale());

                // Get parameters
                // 需要 function 和 prompt
                AIParameters params = configUtils.getFunctionAndPrompt(
                                aiService,
                                aiProperties,
                                localString,
                                aiProperties.getFunctionForPcl(),
                                aiProperties.getPromptPrefixForPcl());

                // Get fields and convert to JSON
                List<ReportFields> reportFields = entityService.selectReportFieldsByReportId(
                                aiService,
                                entityInfo.getId());
                String fieldsJson = jsonUtils.convertFieldsToJson(reportFields);

                // Call AI and process response
                OpenAiChatCompletionOutput aiResult = openAIService.callAIWithFunction(
                                params.getFunction(),
                                fieldsJson,
                                params.getPromptContent());

                // Process PCLs
                List<Pcls> pclsList = new ArrayList<>();
                aiResponseHelper.processResponse(aiResult, null, pclsList, AIConstants.NodeKeys.ITEMS,
                                jsonUtils::createPcl);

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
}
