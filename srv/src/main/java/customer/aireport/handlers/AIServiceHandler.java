package customer.aireport.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.ai.sdk.foundationmodels.openai.OpenAiClient;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionFunction;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionOutput;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionParameters;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionTool;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatMessage;
import com.sap.cds.Result;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.AnalysisResult;
import com.sap.cds.ql.cqn.CqnAnalyzer;

import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import customer.aireport.util.AIReportProperties;
import customer.aireport.util.AIUtil;
import customer.aireport.util.OpenAiChatAssistantMessage2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.sap.ai.sdk.foundationmodels.openai.OpenAiModel.GPT_4O;
import static com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionTool.ToolType.FUNCTION;

import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.ChatService_;
import cds.gen.chatservice.ParameterItems;
import cds.gen.chatservice.ParameterItems_;
import cds.gen.chatservice.ReportsNewRecordContext;
import cds.gen.chatservice.RecordsAdoptContext;
import cds.gen.chatservice.Reports;
import cds.gen.chatservice.ReportsAppendToChatRecordContext;
import cds.gen.chatservice.Reports_;
import cds.gen.chatservice.Records;
import cds.gen.chatservice.Records_;
import cds.gen.chatservice.ReportFields;
import cds.gen.chatservice.ReportFields_;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;

import customer.aireport.dto.FieldSummary;

// import cds.gen.chatservice.NewChatContext;
// import cds.gen.chatservice.Parameters;
// import cds.gen.chatservice.Parameters_;
// import cds.gen.chatservice.Chats_;
// import cds.gen.chatservice.Chats;
// import org.springframework.util.ObjectUtils;
// import com.sap.cds.ql.Insert;
// import com.sap.cds.services.handler.annotations.After;

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

    // @On(event = NewChatContext.CDS_NAME)
    // public void newChat(NewChatContext newChatContext) {
    // Result result =
    // aiService.run(Insert.into(Chats_.class).entry(Chats.create()));
    // Chats chats = result.single(Chats.class);
    // newChatContext.setResult(chats);
    // }

    @On(event = ReportsNewRecordContext.CDS_NAME, entity = Reports_.CDS_NAME)
    public void newMessage(ReportsNewRecordContext reportsNewRecordContext) {

        // Get UUID of Chats Entity
        CqnSelect selectChat = reportsNewRecordContext.getCqn();
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(reportsNewRecordContext.getModel());
        AnalysisResult analysisResult = cqnAnalyzer.analyze(selectChat);
        Map<String, Object> rootKeys = analysisResult.rootKeys();
        String reportsUUID = (String) rootKeys.get("ID");

        // Get Chat history
        CqnSelect selectRecords = Select.from(Records_.class).where(b -> b.report_ID().eq(reportsUUID))
                .orderBy(c -> c.createdAt().asc());
        Result recordSResult = aiService.run(selectRecords);
        List<Records> records = recordSResult.listOf(Records.class);

        Result reportsResult = aiService.run(selectChat);
        Reports report = reportsResult.single(Reports.class);
        boolean isReportActive = report.getIsActiveEntity();

        // 打印 Report 实体的状态
        System.out.println("Report before update - IsActiveEntity: " + report.getIsActiveEntity());

        // Get Prompt from AI service first time
        Locale locale = reportsNewRecordContext.getParameterInfo().getLocale();
        if (locale == null) {
            locale = Locale.of("zh");
        }
        final String localString = locale.getLanguage();

        CqnSelect selectParam = Select.from(ParameterItems_.class).where(
                b -> b.name().eq(aiReportProperties.getPromptPrefixForReport()).and(b.language().eq(localString)));
        // b -> b.name().eq("prompt_report_" + localString));
        Result paramsResult = aiService.run(selectParam);
        if (paramsResult.rowCount() == 0) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Maintain_Parameter",
                    aiReportProperties.getPromptPrefixForReport() + localString);
        }
        ParameterItems paramPrompt = paramsResult.single(ParameterItems.class);

        // Build Chat Completion Parameter
        OpenAiChatCompletionParameters aiChatCompletionParameters = new OpenAiChatCompletionParameters();

        if (records.size() == 0) {
            // parse Prompt and user
            aiChatCompletionParameters.addMessages(new OpenAiChatMessage[] {
                    (new OpenAiChatMessage.OpenAiChatSystemMessage()).setContent(paramPrompt.getValue()) });
            aiChatCompletionParameters.addMessages(new OpenAiChatMessage[] {
                    (new OpenAiChatMessage.OpenAiChatUserMessage()).addText(reportsNewRecordContext.getContent()) });

            // insert prompt into Records Table
            Records recordsSystem = Records.create();
            recordsSystem.setId(UUID.randomUUID().toString());
            recordsSystem.setRole("system");
            recordsSystem.setContent(paramPrompt.getValue());
            recordsSystem.setIsAdopted(false);
            recordsSystem.setReportId(report.getId()); // 设置关联
            recordsSystem.setIsActiveEntity(isReportActive); // 设置为 draft

            // 直接创建 Records
            Result result;
            if (isReportActive) {
                result = aiService.run(Insert.into(Records_.class).entry(recordsSystem));
            } else {
                result = aiServiceDraft.newDraft(Insert.into(Records_.class).entry(recordsSystem));
            }

            System.out.println("Insert Records result - count: " + result.rowCount());
            if (result.rowCount() > 0) {
                Records insertedRecord = result.single(Records.class);
                System.out.println("Inserted Record - IsActiveEntity: " + insertedRecord.getIsActiveEntity());
            }
        } else {
            // Put history message into AI call
            records.forEach((record) -> {
                switch (record.getRole()) {
                    case "system":
                        aiChatCompletionParameters.addMessages(new OpenAiChatMessage[] {
                                (new OpenAiChatMessage.OpenAiChatSystemMessage()).setContent(record.getContent()) });
                    case "user":
                        // if (!ObjectUtils.isEmpty(record.getPrompt())) {
                        // aiChatCompletionParameters.addMessages(new OpenAiChatMessage[] {
                        // (new
                        // OpenAiChatMessage.OpenAiChatSystemMessage()).setContent(record.getPrompt())
                        // });
                        // }
                        aiChatCompletionParameters.addMessages(new OpenAiChatMessage[] {
                                (new OpenAiChatMessage.OpenAiChatUserMessage()).addText(record.getContent()) });
                        break;
                    case "assistant":
                        // (new OpenAiChatAssistantMessage2()).setContent(record.getContent())
                        aiChatCompletionParameters.addMessages(new OpenAiChatMessage[] {
                                (new OpenAiChatAssistantMessage2()).setContent(record.getContent())
                        });
                        break;
                    default:
                        break;
                }

            });
        }

        // call AI Chat Completion
        OpenAiClient aiClient = AIUtil.getAiClientbyModelUsingBTPDestination(GPT_4O);
        OpenAiChatCompletionOutput aiResult = aiClient.chatCompletion(aiChatCompletionParameters);

        // Record User role
        Records recordsUser = Records.create();
        recordsUser.setId(UUID.randomUUID().toString());
        recordsUser.setIsActiveEntity(isReportActive);
        recordsUser.setRole("user");
        recordsUser.setContent(reportsNewRecordContext.getContent());
        recordsUser.setIsAdopted(false);
        recordsUser.setReportId(report.getId());

        // Record assistant role
        Records recordsAssist = Records.create();
        recordsAssist.setId(UUID.randomUUID().toString());
        recordsAssist.setIsActiveEntity(isReportActive);
        recordsAssist.setRole("assistant");
        recordsAssist.setContent(aiResult.getContent());
        recordsAssist.setIsAdopted(false);
        recordsAssist.setCreatedBy("AI");
        recordsAssist.setReportId(report.getId());
        // Insert User Message
        // Insert assistant Message
        // Result userResult =
        // aiServiceDraft.newDraft(Insert.into(Records_.class).entry(recordsUser));
        Result userResult, assistResult;

        if (isReportActive) {
            userResult = aiService.run(Insert.into(Records_.class).entry(recordsUser));
            assistResult = aiService.run(Insert.into(Records_.class).entry(recordsAssist));
        } else {
            userResult = aiServiceDraft.newDraft(Insert.into(Records_.class).entry(recordsUser));
            assistResult = aiServiceDraft.newDraft(Insert.into(Records_.class).entry(recordsAssist));
        }
        Records userRecord = userResult.single(Records.class);
        Records assistRecord = assistResult.single(Records.class);

        // Result assistResult =
        // aiServiceDraft.newDraft(Insert.into(Records_.class).entry(recordsAssist));

        reportsNewRecordContext.setResult(assistRecord);

        // // call AI function for report name
        // if (ObjectUtils.isEmpty(chat.getTitle())) {

        // CqnSelect selectPromptRepname = Select.from(Parameters_.class).where(b ->
        // b.name()
        // .eq(aiReportProperties.getPromptPrefixForReportName() + localString));
        // // .eq("prompt_repname_" + localString));
        // Result resultPromptRepname = aiService.run(selectPromptRepname);
        // OpenAiChatCompletionFunction function;
        // if (resultPromptRepname.rowCount() > 0) {
        // Parameters paramPromptRepname = resultPromptRepname.single(Parameters.class);
        // ObjectMapper objectMapper = new ObjectMapper();
        // try {
        // // Parse JSON from Parameters Table
        // function = objectMapper.readValue(paramPromptRepname.getValue(),
        // OpenAiChatCompletionFunction.class);

        // } catch (JsonProcessingException e) {
        // // TODO Auto-generated catch block
        // // e.printStackTrace();
        // throw new ServiceException(ErrorStatuses.BAD_REQUEST,
        // "Error_When_Parsing_RPName_Parameter", e);

        // // function = new OpenAiChatCompletionFunction();
        // // function.setName("get_report_name")
        // // .setDescription("获取报表名称`report_name`,比如采购订单报表，也可能叫采购订单表")
        // // .setParameters(Map.of("type", "object", "properties",
        // // Map.of("ReportName", Map.of("type", "string", "description", "<报表名称>"))));
        // }
        // } else {
        // throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Maintain_Parameter",
        // aiReportProperties.getPromptPrefixForReportName() + localString);
        // // function = new OpenAiChatCompletionFunction();
        // // function.setName("get_report_name")
        // // .setDescription("获取报表名称`report_name`,比如采购订单报表，也可能叫采购订单表")
        // // .setParameters(Map.of("type", "object", "properties",
        // // Map.of("ReportName", Map.of("type", "string", "description", "<报表名称>"))));
        // }

        // OpenAiChatCompletionTool tool = new OpenAiChatCompletionTool();
        // tool.setType(FUNCTION).setFunction(function);
        // OpenAiChatCompletionParameters reportFunctionParam = new
        // OpenAiChatCompletionParameters();
        // reportFunctionParam
        // .addMessages(
        // new
        // OpenAiChatMessage.OpenAiChatUserMessage().addText(ReportsNewRecordContext.getContent()))
        // .setTools(List.of(tool));

        // OpenAiChatCompletionOutput aiResultforReportName =
        // aiClient.chatCompletion(reportFunctionParam);
        // // if (aiResultforReportName)
        // // aiResultforReportName.getChoices().get(0).
        // // String title;

        // aiResultforReportName.getChoices().forEach(choice -> {
        // if (choice.getFinishReason().equals("tool_calls")) {
        // String reportJson =
        // choice.getMessage().getToolCalls().get(0).getFunction().getArguments();
        // // String title =
        // ObjectMapper objectMapper = new ObjectMapper();
        // try {
        // JsonNode rootNode = objectMapper.readTree(reportJson);
        // chat.setTitle(rootNode.get("ReportName").asText());
        // } catch (JsonProcessingException e) {
        // // TODO Auto-generated catch block
        // // e.printStackTrace();
        // throw new ServiceException(ErrorStatuses.BAD_REQUEST,
        // "Error_When_Parsing_Report_Name_Result",
        // e);

        // }

        // }
        // });

        // }

    }

    @On(event = RecordsAdoptContext.CDS_NAME, entity = Records_.CDS_NAME)
    public void adopt(RecordsAdoptContext adoptContext) {
        // Get Original Record SQL
        CqnSelect selectRecord = adoptContext.getCqn();

        // Read Record Entity and Chat Entity
        Result result = aiService.run(selectRecord);
        Records records = result.single(Records.class);

        // Call AI to Get JSON
        Locale locale = adoptContext.getParameterInfo().getLocale();
        if (locale == null) {
            locale = Locale.of("zh");
        }
        final String localString = locale.getLanguage();

        CqnSelect selectPromptJSON = Select.from(ParameterItems_.class)
                .where(b -> b.name().eq(aiReportProperties.getPromptPrefixForJson()).and(b.language().eq(localString)));
        // .where(b -> b.name().eq("prompt_json_" + localString));
        Result resultPromptJSON = aiService.run(selectPromptJSON);
        OpenAiChatCompletionFunction function;
        if (resultPromptJSON.rowCount() > 0) {
            ParameterItems paramPromptJSON = resultPromptJSON.single(ParameterItems.class);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // Parse JSON from Parameters Table
                function = objectMapper.readValue(paramPromptJSON.getValue(),
                        OpenAiChatCompletionFunction.class);

            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                function = new OpenAiChatCompletionFunction();
                throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Error_When_Parsing_JSON_Parameter", e);
            }
        } else {
            function = new OpenAiChatCompletionFunction();
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Maintain_Parameter",
                    aiReportProperties.getPromptPrefixForJson() + localString);

        }
        OpenAiClient aiClient = AIUtil.getAiClientbyModelUsingBTPDestination(GPT_4O);
        OpenAiChatCompletionTool tool = new OpenAiChatCompletionTool();
        tool.setType(FUNCTION).setFunction(function);
        OpenAiChatCompletionParameters reportFunctionParam = new OpenAiChatCompletionParameters();
        reportFunctionParam
                .addMessages(
                        new OpenAiChatMessage.OpenAiChatUserMessage().addText(records.getContent()))
                .setTools(List.of(tool));

        OpenAiChatCompletionOutput aiResultforReportJSON = aiClient.chatCompletion(reportFunctionParam);

        // Create Report Entity and ReportFieldsEntity
        // Reports reports = Reports.create();
        // reports.setRecordId(records.getId());

        // 先删除原先的ReportFields
        aiService.run(Delete.from(ReportFields_.class).where(b -> b.report_ID().eq(records.getReportId())));

        CqnSelect selectReports = Select.from(Reports_.class)
                .where(b -> b.ID().eq(records.getReportId()).and(b.IsActiveEntity().eq(records.getIsActiveEntity())));
        Result resultReports = aiService.run(selectReports);
        Reports reports = resultReports.single(Reports.class);

        List<ReportFields> fieldsList = new ArrayList<ReportFields>();

        aiResultforReportJSON.getChoices().forEach(choice -> {
            if (choice.getFinishReason().equals("tool_calls")) {
                String reportJson = choice.getMessage().getToolCalls().get(0).getFunction().getArguments();
                // String title =
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    System.out.println("Raw JSON from AI: " + reportJson); // 添加日志
                    JsonNode rootNode = objectMapper.readTree(reportJson);
                    // objectMapper.treeToValue(rootNode, Reports.class);

                    reports.setText(rootNode.get("Reports").get("Text").asText());
                    for (JsonNode arrayItem : rootNode.get("fields")) {
                        ReportFields field = ReportFields.create();
                        field.setCategory(getStringWithDefault(arrayItem, "category", "default_category"));
                        field.setTabFdPos(getIntWithDefault(arrayItem, "TabFdPos", 0));
                        field.setParamText(getStringWithDefault(arrayItem, "ParamText", ""));
                        field.setFieldType(getStringWithDefault(arrayItem, "FieldType", "string"));
                        field.setDisplay(getStringWithDefault(arrayItem, "Display", ""));
                        field.setEnterable(getStringWithDefault(arrayItem, "Enterable", ""));
                        field.setObligatory(getStringWithDefault(arrayItem, "Obligatory", ""));
                        field.setValueHelp(getStringWithDefault(arrayItem, "ValueHelp", ""));
                        field.setToEntityText(getStringWithDefault(arrayItem, "ToEntityText", ""));
                        field.setToEntity(getStringWithDefault(arrayItem, "ToEntity", ""));
                        field.setToFieldText(getStringWithDefault(arrayItem, "ToFieldText", ""));
                        field.setToField(getStringWithDefault(arrayItem, "ToField", ""));
                        field.setIsKey(getStringWithDefault(arrayItem, "IsKey", ""));
                        field.setRequiresCalculation(getStringWithDefault(arrayItem, "RequiresCalculation", ""));
                        field.setCaculationLogic(getStringWithDefault(arrayItem, "CaculationLogic", ""));
                        field.setValueHelpTable(getStringWithDefault(arrayItem, "ValueHelpTable", ""));
                        field.setValueHelpField(getStringWithDefault(arrayItem, "ValueHelpField", ""));
                        field.setSeq(getIntWithDefault(arrayItem, "Seq", 0));

                        fieldsList.add(field);
                    }
                } catch (JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                    throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Error_When_Parsing_JSON_Result",
                            e);

                }

            }
        });

        fieldsList.forEach(field -> {
            field.setReportId(reports.getId());
            field.setIsActiveEntity(reports.getIsActiveEntity()); // 同步父实体状态

            if (reports.getIsActiveEntity()) {
                aiService.run(Insert.into(ReportFields_.class)
                        .entry(field));
                // .columns(
                // ReportFields.CATEGORY,
                // ReportFields.TAB_FD_POS,
                // ReportFields.PARAM_TEXT,
                // // ... 其他字段 ...
                // ReportFields.IS_ACTIVE_ENTITY
                // ));
            } else {
                aiServiceDraft.newDraft(Insert.into(ReportFields_.class)
                        .entry(field));
            }
        });

        // Set Adopt field to "true" for current Record Entity
        records.setIsAdopted(true);
        // records.setReportId(result2.single(Reports.class).getId());
        Result result3;
        if (records.getIsActiveEntity()) {
            result3 = aiService.run(Update.entity(Records_.class).data(records));
        } else {
            result3 = aiServiceDraft.patchDraft(Update.entity(Records_.class).data(records));
        }
        // return report entity
        // adoptContext.setResult(result2.single(Reports.class));
        // adoptContext.setCompleted();
        adoptContext.setResult(result3.single(Records.class));

    }

    @On(event = ReportsAppendToChatRecordContext.CDS_NAME, entity = Reports_.CDS_NAME)
    public void appendToChatRecord(ReportsAppendToChatRecordContext appendToChatRecordContext) {
        // CqnAnalyzer analyzer = CqnAnalyzer.create(request.getModel());
        CqnSelect selectReport = appendToChatRecordContext.getCqn();
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(appendToChatRecordContext.getModel());
        AnalysisResult analysisResult = cqnAnalyzer.analyze(selectReport);
        Map<String, Object> rootKeys = analysisResult.rootKeys();
        String reportsUUID = (String) rootKeys.get("ID");
        boolean isActive = (boolean) rootKeys.get("IsActiveEntity");

        // 获取当前Report的所有字段（参考代码块424-442行）
        Result fieldsResult = (isActive ? aiService : aiServiceDraft).run(
                Select.from(ReportFields_.class).where(b -> b.report_ID().eq(reportsUUID)));
        List<ReportFields> fields = fieldsResult.listOf(ReportFields.class);

        // 转换为JSON（参考代码块378-394行）
        ObjectMapper mapper = new ObjectMapper();
        String fieldsJson;
        try {
            fieldsJson = mapper.writeValueAsString(fields.stream()
                    .map(f -> new FieldSummary(f.getCategory(), f.getTabFdPos(), f.getParamText(), f.getFieldType(),
                            f.getDisplay(), f.getEnterable(), f.getObligatory(), f.getValueHelp(), f.getToEntityText(),
                            f.getToEntity(), f.getToFieldText(), f.getToField(), f.getIsKey(), f.getRequiresCalculation(),
                            f.getCaculationLogic(), f.getValueHelpTable(), f.getValueHelpField(), f.getSeq()))
                    .collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            // throw new ServiceException(ErrorStatuses.SERVER_ERROR, "JSON转换失败");
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "JSON转换失败", e);
        }
        // new FieldSummary(fieldsJson, 0, fieldsJson, fieldsJson, fieldsJson,
        // fieldsJson, fieldsJson, fieldsJson, fieldsJson, fieldsJson, reportsUUID,
        // fieldsJson, 0)

        // 创建新Records记录（参考代码块424-442行和70-81行）
        Records newRecord = Records.create();
        newRecord.setRole("user");
        newRecord.setContent(fieldsJson);
        newRecord.setReportId(reportsUUID);
        newRecord.setIsActiveEntity(isActive);

        Result result = isActive ? aiService.run(Insert.into(Records_.class).entry(newRecord))
                : aiServiceDraft.newDraft(Insert.into(Records_.class).entry(newRecord));

        // appendToChatRecordContext.setCompleted();
        appendToChatRecordContext.setResult(result.single(Records.class));
    }

    // @After(event = CqnService.EVENT_DELETE, entity = Reports_.CDS_NAME)
    // public void afterReportDelete(List<Reports> reports) {
    // // for(Reports report )
    // for (Reports report : reports) {
    // aiService.run(Update.entity(Records_.class).data(Records.IS_ADOPTED, false)
    // .where(b -> b.ID().eq(report.getRecordId())));
    // }
    // }

    private String getStringWithDefault(JsonNode node, String field, String defaultValue) {
        JsonNode value = node.path(field);
        return value.isMissingNode() ? defaultValue : value.asText();
    }

    private int getIntWithDefault(JsonNode node, String field, int defaultValue) {
        JsonNode value = node.path(field);
        return value.isMissingNode() ? defaultValue : value.asInt();
    }
}
