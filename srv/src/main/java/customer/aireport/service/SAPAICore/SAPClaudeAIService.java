package customer.aireport.service.SAPAICore;

import static com.sap.ai.sdk.foundationmodels.openai.OpenAiModel.GPT_4O;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import customer.aireport.dto.CommonAIMessage;
import customer.aireport.exception.BusinessException;
import customer.aireport.factory.AIResponseHandlerFactory;
import customer.aireport.factory.SAPClaudeAIMessageFactory;
import customer.aireport.helper.AIResponseHelper;
import customer.aireport.model.AIParameters;
import customer.aireport.model.EntityInfo;
import customer.aireport.service.AIService.AIServiceI;
import customer.aireport.service.SAPAICore.claude.ClaudeAiClient;
import customer.aireport.service.SAPAICore.claude.ClaudeAiModel;
import customer.aireport.service.SAPAICore.claude.generated.model.InvokeRequest;
import customer.aireport.service.SAPAICore.claude.generated.model.InvokeResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.Tool;
import customer.aireport.util.ConfigUtils;
import customer.aireport.util.JsonUtils;

@Service
public class SAPClaudeAIService implements AIServiceI {
        private final ClaudeAiModel DEFAULT_MODEL = ClaudeAiModel.CLAUDE_3_5_SONNET;

        @Autowired
        private AIProperties aiProperties; // Changed from AIReportProperties

        @Autowired
        private AIServiceKeysConfig aiServiceKeys; // Changed from AIServiceKeys
        // public void setAiProperties(AIProperties aiProperties) { // Changed method
        // name and parameter type
        // this.aiProperties = aiProperties; // Changed from AIUtil
        // }

        @Autowired
        private SAPClaudeAIMessageFactory messageFactory;

        @Autowired
        private AIResponseHelper aiResponseHelper;

        @Autowired
        private ConfigUtils configUtils; // Add ConfigUtils injection

        @Autowired
        private JsonUtils jsonUtils;

        @Autowired
        private AIResponseHandlerFactory aiResponseHandlerFactory;

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
        public InvokeResponse callAIWithFunction(
                        Tool tool,
                        String content) {
                return callAIWithFunction(tool, content, "");
        }

        /**
         * Call Claude AI with a tool, content and prompt prefix
         */
        public InvokeResponse callAIWithFunction(
                        Tool tool,
                        String content,
                        String promptPrefix) {

                ClaudeAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);

                InvokeRequest request = new InvokeRequest()
                                .anthropicVersion("bedrock-2023-05-31")
                                .maxTokens(aiServiceKeys.getClaudeMaxTokens())
                                .temperature(new BigDecimal("0.7"));
                // Add the tool to request
                request.addToolsItem(tool);

                request.setSystem(promptPrefix);
                // Create user message with content
                request.addMessagesItem(
                                // messageFactory.createUserMessage(promptPrefix + content)
                                messageFactory.createUserMessage(content));

                return aiClient.chatCompletion(request);
        }

        public void callAICompletion(
                        List<CommonAIMessage> messages,
                        Reports report,
                        String userContent,
                        EntityInfo entityInfo,
                        ReportsNewRecordContext context) {

                InvokeRequest request = new InvokeRequest()
                                .anthropicVersion("bedrock-2023-05-31")
                                .maxTokens(aiServiceKeys.getClaudeMaxTokens())
                                .temperature(new BigDecimal("0.7"));

                // Extract system message if present
                String systemMessage = messages.stream()
                                .filter(msg -> AIConstants.Roles.SYSTEM.equals(msg.role()))
                                .map(CommonAIMessage::message)
                                .findFirst()
                                .orElse(null);

                if (systemMessage != null) {
                        request.setSystem(systemMessage);
                }

                // Add other messages (user and assistant)
                messages.stream()
                                .filter(msg -> !AIConstants.Roles.SYSTEM.equals(msg.role()))
                                .map(msg -> switch (msg.role()) {
                                        case AIConstants.Roles.USER ->
                                                messageFactory.createUserMessage(msg.message());
                                        case AIConstants.Roles.ASSISTANT ->
                                                messageFactory.createAssistantMessage(msg.message());
                                        default -> throw new BusinessException(AIConstants.Messages.UNEXPECTED_ROLE +
                                                        msg.role());
                                })
                                .forEach(request::addMessagesItem);

                ClaudeAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);
                InvokeResponse rawResult = aiClient.chatCompletion(request);

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
                Tool tool = configUtils.getFunction(
                                readService,
                                adoptContext.getParameterInfo().getLocale(),
                                aiProperties.getClaude().getFunctionForJson(),
                                Tool.class);

                // Call AI to process record content
                InvokeResponse rawResult = callAIWithFunction(
                                tool,
                                originalRecord.getContent(),
                                "");

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
                AIParameters<Tool> params = configUtils.getFunctionAndPrompt(
                                readService,
                                generatePCLContext.getParameterInfo().getLocale(),
                                aiProperties.getClaude().getFunctionForPcl(),
                                aiProperties.getClaude().getPromptPrefixForPcl(),
                                Tool.class);

                // Call AI and process response
                InvokeResponse rawResult = callAIWithFunction(
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
                AIParameters<Tool> params = configUtils.getFunctionAndPrompt(
                                readService,
                                generateCDSContext.getParameterInfo().getLocale(),
                                aiProperties.getClaude().getFunctionForCds(),
                                aiProperties.getClaude().getPromptPrefixForCds(),
                                Tool.class);

                // Call AI service
                InvokeResponse rawResult = callAIWithFunction(
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
}