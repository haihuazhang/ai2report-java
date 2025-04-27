package customer.aireport.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.sap.cds.Result;

import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.Records;
import cds.gen.chatservice.Reports;
import cds.gen.chatservice.ReportsNewRecordContext;
import customer.aireport.constant.AIConstants;
import customer.aireport.dto.AIResponse;
import customer.aireport.exception.BusinessException;
import customer.aireport.factory.RecordFactory;
import customer.aireport.model.EntityInfo;
import customer.aireport.processor.AIResponseProcessor;
import customer.aireport.service.EntityService;
import customer.aireport.util.JsonUtils;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AIResponseHelper {    
    
    
    private final JsonUtils jsonUtils;
    private final RecordFactory recordFactory;
    private final EntityService entityService;
    private final ChatService aiService;
    private final ChatService.Draft aiServiceDraft;

    public AIResponseHelper(
            JsonUtils jsonUtils,
            RecordFactory recordFactory,
            EntityService entityService,
            ChatService aiService,
            ChatService.Draft aiServiceDraft) {
        this.jsonUtils = jsonUtils;
        this.recordFactory = recordFactory;
        this.entityService = entityService;
        this.aiService = aiService;
        this.aiServiceDraft = aiServiceDraft;
    }

    // public AIResponseHelper(JsonUtils jsonUtils) {
    // this.jsonUtils = jsonUtils;
    // }

    public <T> void processResponse(
            AIResponse aiResult,
            Reports report,
            List<T> resultList,
            String nodeKey,
            AIResponseProcessor<T> processor) {
        // if ("tool_calls".equals(aiResult.getFinishReason())) {
        String jsonString = aiResult.getContent();
        JsonNode rootNode = jsonUtils.parseJson(jsonString);
        if (report != null) {
            jsonUtils.setReportText(report, rootNode);
        }

        JsonNode itemsNode = rootNode.get(nodeKey);
        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode arrayItem : itemsNode) {
                resultList.add(processor.process(arrayItem));
            }
        }
        // }
    }

    public void handleChatResponse(
            AIResponse aiResult,
            Reports report,
            String userContent,
            EntityInfo entityInfo,
            ReportsNewRecordContext context) {
        Records userRecord = recordFactory.createUserRecord(
                userContent,
                entityInfo.getId(),
                entityInfo.getIsActiveEntity());
        Records assistRecord = recordFactory.createAssistantRecord(
                aiResult.getContent(),
                entityInfo.getId(),
                entityInfo.getIsActiveEntity());

        entityService.insertRecord(aiService, aiServiceDraft, userRecord, entityInfo.getIsActiveEntity());

        try {
            Thread.sleep(AIConstants.CHAT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(AIConstants.Messages.THREAD_INTERRUPTED, e);
        }

        Result assistResult = entityService.insertRecord(
                aiService,
                aiServiceDraft,
                assistRecord,
                entityInfo.getIsActiveEntity());
        if (context != null) {
            context.setResult(assistResult.single(Records.class));
        }

    }

    public void handleCDSResponse(
            AIResponse aiResult,
            Reports report) {
        // if ("tool_calls".equals(aiResult.getFinishReason())) {
        // String jsonString =
        // aiResult.getToolCalls().get(0).getFunction().getArguments();
        String jsonString = aiResult.getContent();
        JsonNode rootNode = jsonUtils.parseJson(jsonString);

        // Update CDS fields in Reports
        report.setCds1(rootNode.get("CDS1").asText());
        report.setCds2(rootNode.get("CDS2").asText());
        report.setCds3(rootNode.get("CDS3").asText());
        report.setCds4(rootNode.get("CDS4").asText());

        // Persist changes using service layer
        entityService.updateReport(aiService, aiServiceDraft, report);
        // }
    }
}