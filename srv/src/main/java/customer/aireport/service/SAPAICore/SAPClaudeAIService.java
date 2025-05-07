package customer.aireport.service.SAPAICore;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sap.ai.sdk.core.AiCoreService;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;

import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.Pcls;
import cds.gen.chatservice.Records;
import cds.gen.chatservice.RecordsAdoptContext;
import cds.gen.chatservice.ReportFields;
import cds.gen.chatservice.Reports;
import cds.gen.chatservice.ReportsGenerateCDSContext;
import cds.gen.chatservice.ReportsGeneratePCLContext;
import cds.gen.chatservice.ReportsNewRecordContext;
import customer.aireport.config.AIProperties;
import customer.aireport.config.AIServiceKeysConfig;
import customer.aireport.constant.AIConstants;
import customer.aireport.constant.AIServiceType;
import customer.aireport.dto.AIResponse;
import customer.aireport.exception.BusinessException;
import customer.aireport.factory.AIResponseHandlerFactory;
import customer.aireport.factory.SAPClaudeAIMessageFactory;
import customer.aireport.helper.AIResponseHelper;
import customer.aireport.model.AIParameters;
import customer.aireport.model.CommonAIMessage;
import customer.aireport.model.EntityInfo;
import customer.aireport.model.StreamChatRequest;
import customer.aireport.service.AIService.AIServiceI;
import customer.aireport.service.SAPAICore.claude.ClaudeAiClient;
import customer.aireport.service.SAPAICore.claude.ClaudeAiModel;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseRequest;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseRequestInferenceConfig;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseRequestToolConfig;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseTool;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseToolChoice;
import customer.aireport.service.SAPAICore.claude.generated.model.SpecificToolChoice;
import customer.aireport.util.ConfigUtils;
import customer.aireport.util.JsonUtils;

@Service
public class SAPClaudeAIService implements AIServiceI {
    private final ClaudeAiModel DEFAULT_MODEL = ClaudeAiModel.CLAUDE_3_7_SONNET;
    private final AIProperties aiProperties;
    private final AIServiceKeysConfig aiServiceKeys;
    private final SAPClaudeAIMessageFactory messageFactory;
    private final AIResponseHelper aiResponseHelper;
    private final ConfigUtils configUtils;
    private final JsonUtils jsonUtils;
    private final AIResponseHandlerFactory aiResponseHandlerFactory;

    public SAPClaudeAIService(
            AIProperties aiProperties,
            AIServiceKeysConfig aiServiceKeys,
            SAPClaudeAIMessageFactory messageFactory,
            AIResponseHelper aiResponseHelper,
            ConfigUtils configUtils,
            JsonUtils jsonUtils,
            AIResponseHandlerFactory aiResponseHandlerFactory) {
        this.aiProperties = aiProperties;
        this.aiServiceKeys = aiServiceKeys;
        this.messageFactory = messageFactory;
        this.aiResponseHelper = aiResponseHelper;
        this.configUtils = configUtils;
        this.jsonUtils = jsonUtils;
        this.aiResponseHandlerFactory = aiResponseHandlerFactory;
    }

    public ClaudeAiClient getAiClientbyModelUsingBTPDestination(@Nonnull ClaudeAiModel foundationModel) {
                // build api destination
                Destination destination = DestinationAccessor.getDestination(aiServiceKeys.getAiCoreDestination());
                AiCoreService aiCoreService = new AiCoreService().withBaseDestination(destination.asHttp());
                Destination destinationWithDeployment = aiCoreService.getInferenceDestination()
                                .forModel(foundationModel);
                return ClaudeAiClient.withCustomDestination(destinationWithDeployment);
        }

        /**
         * Call Claude AI with a tool and content
         */
        public ConverseResponse callAIWithFunction(
                        ConverseTool tool,
                        String content) {
                return callAIWithFunction(tool, content, "");
        }

        /**
         * Call Claude AI with a tool, content and prompt prefix
         */
        public ConverseResponse callAIWithFunction(
                        ConverseTool tool,
                        String content,
                        String promptPrefix) {

                ClaudeAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);

                ConverseRequest request = new ConverseRequest();

                // Set inference config
                ConverseRequestInferenceConfig inferenceConfig = new ConverseRequestInferenceConfig()
                                .maxTokens(aiServiceKeys.getClaudeMaxTokens())
                                .temperature(new BigDecimal("0.7"));
                request.setInferenceConfig(inferenceConfig);

                // Set tool config
                ConverseRequestToolConfig toolConfig = new ConverseRequestToolConfig();
                toolConfig.addToolsItem(tool);
                toolConfig.setToolChoice(new ConverseToolChoice().tool(new SpecificToolChoice().name(tool.getToolSpec().getName())));
                request.setToolConfig(toolConfig);

                // Set system message if provided
                if (promptPrefix != null && !promptPrefix.isEmpty()) {
                        request.addSystemItem(messageFactory.createSystemBlock(promptPrefix));
                }

                // Add user message
                request.addMessagesItem(messageFactory.createUserMessage(content));

                return aiClient.chatCompletion(request);
        }

        public void callAICompletion(
                        List<CommonAIMessage> messages,
                        Reports report,
                        String userContent,
                        EntityInfo entityInfo,
                        ReportsNewRecordContext context) {

                ConverseRequest request = new ConverseRequest();

                // Set inference config
                ConverseRequestInferenceConfig inferenceConfig = new ConverseRequestInferenceConfig()
                                .maxTokens(aiServiceKeys.getClaudeMaxTokens())
                                .temperature(new BigDecimal("0.7"));
                request.setInferenceConfig(inferenceConfig);

                // Extract system message if present
                String systemMessage = messages.stream()
                                .filter(msg -> AIConstants.Roles.SYSTEM.equals(msg.role()))
                                .map(CommonAIMessage::message)
                                .findFirst()
                                .orElse(null);

                if (systemMessage != null) {
                        request.addSystemItem(messageFactory.createSystemBlock(systemMessage));
                }

                // Add other messages (user and assistant)
                messages.stream()
                                .filter(msg -> !AIConstants.Roles.SYSTEM.equals(msg.role()))
                                .forEach(msg -> {
                                        if (AIConstants.Roles.USER.equals(msg.role())) {
                                                request.addMessagesItem(messageFactory.createUserMessage(msg.message()));
                                        } else if (AIConstants.Roles.ASSISTANT.equals(msg.role())) {
                                                request.addMessagesItem(messageFactory.createAssistantMessage(msg.message()));
                                        } else {
                                                throw new BusinessException(AIConstants.Messages.UNEXPECTED_ROLE +
                                                                msg.role());
                                        }
                                });

                ClaudeAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);
                ConverseResponse rawResult = aiClient.chatCompletion(request);

                // 使用适配器转换响应
                AIResponse aiResponse = aiResponseHandlerFactory
                                .getHandler(AIServiceType.SAPCLAUDE)
                                .processResponse(rawResult);

                aiResponseHelper.handleChatResponse(
                                aiResponse,
                                report,
                                context.getContent(),
                                entityInfo,
                                context);
        }

        @Override
        public List<ReportFields> callAIforAdopt(ChatService readService, RecordsAdoptContext adoptContext,
                        Records originalRecord,
                        Reports report) {
                // TODO Auto-generated method stub
                // throw new UnsupportedOperationException("Unimplemented method
                // 'callAIforAdopt'");
                // Get AI function for JSON processing
                ConverseTool tool = configUtils.getFunction(
                                readService,
                                adoptContext.getParameterInfo().getLocale(),
                                aiProperties.getClaude().getFunctionForJson(),
                                ConverseTool.class);

                // Call AI to process record content
                ConverseResponse rawResult = callAIWithFunction(
                                tool,
                                "get complete json from prompt",
                                originalRecord.getContent());

                // 使用适配器转换响应
                AIResponse aiResponse = aiResponseHandlerFactory
                                .getHandler(AIServiceType.SAPCLAUDE)
                                .processResponse(rawResult);

                List<ReportFields> fieldsList = new ArrayList<>();
                aiResponseHelper.processResponse(aiResponse, report, fieldsList, AIConstants.NodeKeys.FIELDS,
                                jsonUtils::createReportField);

                return fieldsList;

        }

        @Override
        public List<Pcls> callAIforGeneratePCL(String fieldsInJSON, ChatService readService,
                        ReportsGeneratePCLContext generatePCLContext) {
                // TODO Auto-generated method stub
                // throw new UnsupportedOperationException("Unimplemented method
                // 'callAIforGeneratePCL'");
                AIParameters<ConverseTool> params = configUtils.getFunctionAndPrompt(
                                readService,
                                generatePCLContext.getParameterInfo().getLocale(),
                                aiProperties.getClaude().getFunctionForPcl(),
                                aiProperties.getClaude().getPromptPrefixForPcl(),
                                ConverseTool.class);

                // Call AI and process response
                ConverseResponse rawResult = callAIWithFunction(
                                params.getFunction(),
                                fieldsInJSON,
                                params.getPromptContent());

                // 使用适配器转换响应
                AIResponse aiResponse = aiResponseHandlerFactory
                                .getHandler(AIServiceType.SAPCLAUDE)
                                .processResponse(rawResult);

                // Process PCLs
                List<Pcls> pclsList = new ArrayList<>();
                aiResponseHelper.processResponse(
                                aiResponse,
                                null,
                                pclsList,
                                "pcl", // Changed from AIConstants.NodeKeys.ITEMS to match function schema
                                jsonUtils::createPcl);
                return pclsList;
        }

        @Override
        public void callAIforGenerateCDS(String fieldsInJSON, ChatService readService,
                        ReportsGenerateCDSContext generateCDSContext, Reports report) {
                // TODO Auto-generated method stub
                // throw new UnsupportedOperationException("Unimplemented method
                // 'callAIforGenerateCDS'");
                // Get AI parameters
                AIParameters<ConverseTool> params = configUtils.getFunctionAndPrompt(
                                readService,
                                generateCDSContext.getParameterInfo().getLocale(),
                                aiProperties.getClaude().getFunctionForCds(),
                                aiProperties.getClaude().getPromptPrefixForCds(),
                                ConverseTool.class);

                // Call AI service
                ConverseResponse rawResult = callAIWithFunction(
                                params.getFunction(),
                                fieldsInJSON,
                                params.getPromptContent());

                // 使用适配器转换响应
                AIResponse aiResponse = aiResponseHandlerFactory
                                .getHandler(AIServiceType.SAPCLAUDE)
                                .processResponse(rawResult);

                // Process response and update CDS
                aiResponseHelper.handleCDSResponse(aiResponse, report);

        }

        @Override
        public SseEmitter callAIforStream(List<CommonAIMessage> messages, Reports report, StreamChatRequest request) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'callAIforStream'");
        }
}