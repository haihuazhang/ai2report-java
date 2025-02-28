package customer.aireport.handlers;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionOutput;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionParameters;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage;
import com.sap.cds.Result;

import com.sap.cds.ql.Select;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import customer.aireport.exception.AIServiceException;
import customer.aireport.util.AIMessageFactory;
import customer.aireport.util.AIReportProperties;
import customer.aireport.util.AIUtil;
import customer.aireport.util.ConfigurationUtil;
import customer.aireport.util.ConfigurationUtil.AIParameters;


import com.fasterxml.jackson.databind.JsonNode;

import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.ChatService_;
import cds.gen.chatservice.Pcls;
import cds.gen.chatservice.ReportsNewRecordContext;
import cds.gen.chatservice.RecordsAdoptContext;
import cds.gen.chatservice.Reports;
import cds.gen.chatservice.ReportsAppendToChatRecordContext;
import cds.gen.chatservice.ReportsGeneratePCLContext;
import cds.gen.chatservice.Reports_;
import cds.gen.chatservice.Records;
import cds.gen.chatservice.Records_;
import cds.gen.chatservice.ReportFields;
import customer.aireport.util.JsonParseUtil;
import customer.aireport.util.RequestAnalyzeUtil;
import customer.aireport.util.RequestAnalyzeUtil.EntityInfo;
import customer.aireport.util.EntityServiceUtil;
import customer.aireport.util.RecordFactory;


@Component
@ServiceName(value = ChatService_.CDS_NAME)
public class AIServiceHandler implements EventHandler {

    @Autowired
    @Qualifier(ChatService_.CDS_NAME)
    ChatService aiService;

    @Autowired
    @Qualifier(ChatService_.CDS_NAME)
    private ChatService.Draft aiServiceDraft; // 改用 ChatService.Draft

    @Autowired
    private AIReportProperties aiReportProperties;

    @On(event = ReportsNewRecordContext.CDS_NAME, entity = Reports_.CDS_NAME)
    public void newMessage(ReportsNewRecordContext reportsNewRecordContext) {
        // Get UUID and IsActiveEntity
        EntityInfo entityInfo = RequestAnalyzeUtil.analyzeRequest(
            reportsNewRecordContext.getCqn(), 
            reportsNewRecordContext.getModel()
        );
        String reportsUUID = entityInfo.getId();
        Boolean isActiveEntity = entityInfo.getIsActiveEntity();

        // Get Chat history and report
        List<Records> records = EntityServiceUtil.selectRecordsByReportId(aiService, reportsUUID);
        Reports report = EntityServiceUtil.selectSingle(
            aiService, 
            reportsNewRecordContext.getCqn(), 
            Reports.class, 
            "Report not found"
        );

        // Get Prompt configuration
        String localString = RequestAnalyzeUtil.getLocaleString(reportsNewRecordContext.getParameterInfo().getLocale());
        AIParameters params = ConfigurationUtil.getParameters(
            aiService,
            aiReportProperties,
            localString,
            aiReportProperties.getPromptPrefixForReport(),
            aiReportProperties.getPromptPrefixForReport()
        );

        // Build Chat Completion Parameter
        OpenAiChatCompletionParameters aiChatCompletionParameters = new OpenAiChatCompletionParameters();

        if (records.isEmpty()) {
            handleNewChat(aiChatCompletionParameters, params, reportsNewRecordContext, report, isActiveEntity);
        } else {
            handleExistingChat(aiChatCompletionParameters, records);
        }

        // Call AI and handle response
        OpenAiChatCompletionOutput aiResult = AIUtil.callAICompletion(aiChatCompletionParameters);
        handleAIResponse(aiResult, report, reportsNewRecordContext, isActiveEntity);
    }

    private void handleNewChat(OpenAiChatCompletionParameters params, AIParameters aiParams, 
            ReportsNewRecordContext context, Reports report, Boolean isActiveEntity) {
        params.addMessages(AIMessageFactory.createSystemMessage(aiParams.getPromptContent()));
        params.addMessages(AIMessageFactory.createUserMessage(context.getContent()));

        Records systemRecord = RecordFactory.createSystemRecord(
            aiParams.getPromptContent(), 
            report.getId(), 
            isActiveEntity
        );
        EntityServiceUtil.insertRecord(aiService, aiServiceDraft, systemRecord, isActiveEntity);
    }

    private void handleExistingChat(OpenAiChatCompletionParameters params, List<Records> records) {
        records.forEach(record -> {
            OpenAiChatMessage[] message = switch (record.getRole()) {
                case "system" -> AIMessageFactory.createSystemMessage(record.getContent());
                case "user" -> AIMessageFactory.createUserMessage(record.getContent());
                case "assistant" -> AIMessageFactory.createAssistantMessage(record.getContent());
                default -> throw new AIServiceException("Unexpected_Role: " + record.getRole());
            };
            params.addMessages(message);
        });
    }

    private void handleAIResponse(OpenAiChatCompletionOutput aiResult, Reports report, 
            ReportsNewRecordContext context, Boolean isActiveEntity) {
        Records userRecord = RecordFactory.createUserRecord(context.getContent(), report.getId(), isActiveEntity);
        Records assistRecord = RecordFactory.createAssistantRecord(aiResult.getContent(), report.getId(), isActiveEntity);

        Result userResult = EntityServiceUtil.insertRecord(aiService, aiServiceDraft, userRecord, isActiveEntity);
        
        try {
            Thread.sleep(1000); // 等待1秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AIServiceException("Thread_Interrupted_While_Waiting", e);
        }
        
        Result assistResult = EntityServiceUtil.insertRecord(aiService, aiServiceDraft, assistRecord, isActiveEntity);

        context.setResult(assistResult.single(Records.class));
    }

    private <T> void processAIResponse(OpenAiChatCompletionOutput aiResult, Reports report,
            List<T> resultList, String nodeKey, AIResponseProcessor<T> processor) {
        aiResult.getChoices().stream()
            .filter(choice -> "tool_calls".equals(choice.getFinishReason()))
            .findFirst()
            .ifPresent(choice -> {
                String jsonString = choice.getMessage().getToolCalls().get(0).getFunction().getArguments();
                System.out.println("Raw JSON from AI: " + jsonString);
                
                JsonNode rootNode = JsonParseUtil.parseJson(jsonString);
                if (report != null) {
                    JsonParseUtil.setReportText(report, rootNode);
                }
                
                JsonNode itemsNode = rootNode.get(nodeKey);
                if (itemsNode != null && itemsNode.isArray()) {
                    for (JsonNode arrayItem : itemsNode) {
                        resultList.add(processor.process(arrayItem));
                    }
                }
            });
    }

    @FunctionalInterface
    private interface AIResponseProcessor<T> {
        T process(JsonNode arrayItem);
    }

    @On(event = RecordsAdoptContext.CDS_NAME, entity = Records_.CDS_NAME)
    public void adopt(RecordsAdoptContext adoptContext) {
        // 获取原始记录
        Records record = EntityServiceUtil.selectSingle(
            aiService, 
            adoptContext.getCqn(), 
            Records.class,
            "Record not found"
        );

        // 获取语言设置和函数参数
        String localString = RequestAnalyzeUtil.getLocaleString(adoptContext.getParameterInfo().getLocale());
        AIParameters params = ConfigurationUtil.getParameters(
            aiService,
            aiReportProperties,
            localString,
            aiReportProperties.getPromptPrefixForJson(),
            aiReportProperties.getPromptPrefixForJson()  // 这里重复使用同一个参数，因为JSON解析不需要额外的prompt
        );

        // 调用 AI 获取 JSON
        OpenAiChatCompletionOutput aiResult = AIUtil.callAIWithFunction(
            params.getFunction(),
            record.getContent(),
            params.getPromptContent()
        );

        // 获取报告实体
        Reports report = EntityServiceUtil.selectSingle(
            aiService,
            Select.from(Reports_.class)
                .where(b -> b.ID().eq(record.getReportId())
                    .and(b.IsActiveEntity().eq(record.getIsActiveEntity()))),
            Reports.class,
            "Report not found"
        );

        // 删除原有的字段并处理 AI 响应
        List<ReportFields> fieldsList = new ArrayList<>();
        EntityServiceUtil.deleteReportFieldsByReportId(aiService, record.getReportId());
        processAIResponse(aiResult, report, fieldsList, "fields", JsonParseUtil::createReportField);

        // 插入新字段并更新状态
        EntityServiceUtil.batchInsert(
            aiService,
            aiServiceDraft,
            fieldsList,
            record.getReportId(),
            record.getIsActiveEntity()
        );
        EntityServiceUtil.updateRecordStatus(aiService, aiServiceDraft, record);

        adoptContext.setResult(record);
    }

    @On(event = ReportsAppendToChatRecordContext.CDS_NAME, entity = Reports_.CDS_NAME)
    public void appendToChatRecord(ReportsAppendToChatRecordContext appendToChatRecordContext) {
        EntityInfo entityInfo = RequestAnalyzeUtil.analyzeRequest(
            appendToChatRecordContext.getCqn(), 
            appendToChatRecordContext.getModel()
        );
        
        List<ReportFields> fields = EntityServiceUtil.selectReportFieldsByReportId(
            entityInfo.getIsActiveEntity() ? aiService : aiServiceDraft,
            entityInfo.getId()
        );
        
        Records newRecord = RecordFactory.createUserRecord(
            JsonParseUtil.convertFieldsToJson(fields),
            entityInfo.getId(),
            entityInfo.getIsActiveEntity()
        );

        Result result = EntityServiceUtil.insertRecord(
            aiService, 
            aiServiceDraft, 
            newRecord, 
            entityInfo.getIsActiveEntity()
        );
        
        appendToChatRecordContext.setResult(result.single(Records.class));
    }

    @On(event = ReportsGeneratePCLContext.CDS_NAME, entity = Reports_.CDS_NAME)
    public void generatePCL(ReportsGeneratePCLContext generatePCLContext) {
        EntityInfo entityInfo = RequestAnalyzeUtil.analyzeRequest(
            generatePCLContext.getCqn(), 
            generatePCLContext.getModel()
        );

        String localString = RequestAnalyzeUtil.getLocaleString(generatePCLContext.getParameterInfo().getLocale());
        
        // Get parameters
        AIParameters params = ConfigurationUtil.getParameters(
            aiService,
            aiReportProperties,
            localString,
            aiReportProperties.getFunctionForPCL(),
            aiReportProperties.getPromptPrefixForPCL()
        );

        // Get fields and convert to JSON
        List<ReportFields> reportFields = EntityServiceUtil.selectReportFieldsByReportId(
            aiService, 
            entityInfo.getId()
        );
        String fieldsJson = JsonParseUtil.convertFieldsToJson(reportFields);

        // Call AI and process response
        OpenAiChatCompletionOutput aiResult = AIUtil.callAIWithFunction(
            params.getFunction(),
            fieldsJson,
            params.getPromptContent()
        );

        // Process PCLs
        List<Pcls> pclsList = new ArrayList<>();
        processAIResponse(aiResult, null, pclsList, "items", JsonParseUtil::createPcl);

        // Delete old and insert new PCLs
        EntityServiceUtil.deletePclsByReportId(aiService, entityInfo.getId());
        EntityServiceUtil.batchInsert(
            aiService, 
            aiServiceDraft, 
            pclsList, 
            entityInfo.getId(), 
            entityInfo.getIsActiveEntity()
        );

        generatePCLContext.setCompleted();
    }
}
