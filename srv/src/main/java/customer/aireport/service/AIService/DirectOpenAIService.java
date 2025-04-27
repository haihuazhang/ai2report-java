package customer.aireport.service.AIService;

// import static com.sap.ai.sdk.foundationmodels.openai.OpenAiModel.GPT_4O;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.Pcls;
import cds.gen.chatservice.Records;
import cds.gen.chatservice.RecordsAdoptContext;
import cds.gen.chatservice.Reports;
import cds.gen.chatservice.ReportFields;
import cds.gen.chatservice.ReportsGenerateCDSContext;
import cds.gen.chatservice.ReportsGeneratePCLContext;
import cds.gen.chatservice.ReportsNewRecordContext;
import customer.aireport.config.AIProperties; // Changed from AIReportProperties
import customer.aireport.config.AIServiceKeysConfig; // Changed from AIServiceKeys
import customer.aireport.constant.AIConstants;
import customer.aireport.constant.AIServiceType;
import customer.aireport.dto.AIResponse;
import customer.aireport.dto.CommonAIMessage;
import customer.aireport.exception.BusinessException;
import customer.aireport.factory.AIResponseHandlerFactory;
import customer.aireport.factory.SAPOpenAIMessageFactory;
import customer.aireport.helper.AIResponseHelper;
import customer.aireport.model.AIParameters;

import customer.aireport.model.EntityInfo;
import customer.aireport.model.StreamChatRequest;
import customer.aireport.util.ConfigUtils;
import customer.aireport.util.JsonUtils;

// Rename from AIUtil.java
// @Service("aiCoreOpenAIService") // Changed bean name to avoid conflict
public class DirectOpenAIService implements AIServiceI {

        // private final ChatModel DEFAULT_MODEL = ChatModel.GPT_4O; // Changed from
        // OpenAiModel to ChatModel

        // @Autowired
        // private OpenAIClient openAIClient; // OpenAIClient

        @Autowired
        private AIProperties aiProperties; // Changed from AIReportProperties

        @Autowired
        private AIServiceKeysConfig aiServiceKeys; // Changed from AIServiceKeys

        @Autowired
        private OpenAiChatModel chatModel;

        @Autowired
        private SAPOpenAIMessageFactory messageFactory;

        @Autowired
        private AIResponseHelper aiResponseHelper;

        @Autowired
        private ConfigUtils configUtils; // Add ConfigUtils injection

        @Autowired
        private JsonUtils jsonUtils;

        @Autowired
        private AIResponseHandlerFactory aiResponseHandlerFactory;

        @Override
        public void callAICompletion(List<CommonAIMessage> messages, Reports report, String userContent,
                        EntityInfo entityInfo, ReportsNewRecordContext context) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'callAICompletion'");
        }

        @Override
        public List<ReportFields> callAIforAdopt(ChatService readService, RecordsAdoptContext context,
                        Records originalRecord, Reports report) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'callAIforAdopt'");
        }

        @Override
        public List<Pcls> callAIforGeneratePCL(String fieldsInJSON, ChatService readService,
                        ReportsGeneratePCLContext generatePCLContext) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'callAIforGeneratePCL'");
        }

        @Override
        public void callAIforGenerateCDS(String fieldsInJSON, ChatService readService,
                        ReportsGenerateCDSContext generateCDSContext, Reports report) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'callAIforGenerateCDS'");
        }

        @Override
        public SseEmitter callAIforStream(List<CommonAIMessage> messages, Reports report, StreamChatRequest request) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'callAIforStream'");
        }

        

        // public OpenAiClient getAiClientbyModelUsingBTPDestination(@Nonnull
        // OpenAiModel foundationModel) {
        // // build api destination
        // Destination destination =
        // DestinationAccessor.getDestination(aiServiceKeys.getAiCoreDestination());
        // AiCoreService aiCoreService = new
        // AiCoreService().withBaseDestination(destination.asHttp());
        // Destination destinationWithDeployment =
        // aiCoreService.getInferenceDestination()
        // .forModel(foundationModel);
        // return OpenAiClient.withCustomDestination(destinationWithDeployment);
        // }

        // public OpenAiChatCompletionOutput callAIWithFunction(
        //                 OpenAiChatCompletionFunction function,
        //                 String content) {
        //         return callAIWithFunction(function, content, "");
        // }

        // public OpenAiChatCompletionOutput callAIWithFunction(
        //                 OpenAiChatCompletionFunction function,
        //                 String content,
        //                 String promptPrefix) {

        //         OpenAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);
        //         OpenAiChatCompletionTool tool = new OpenAiChatCompletionTool()
        //                         .setType(ToolType.FUNCTION)
        //                         .setFunction(function);

        //         OpenAiChatCompletionParameters params = new OpenAiChatCompletionParameters()
        //                         .addMessages(
        //                                         new OpenAiChatMessage.OpenAiChatUserMessage()
        //                                                         .addText(promptPrefix + content))
        //                         .setTools(List.of(tool));

        //         return aiClient.chatCompletion(params);
        // }

        // public void callAICompletion(
        //                 List<CommonAIMessage> messages,
        //                 Reports report,
        //                 String userContent,
        //                 EntityInfo entityInfo,
        //                 ReportsNewRecordContext context) {
        //         // ChatCompletionCreateParams chatCompletionCreateParams =
        //         // ChatCompletionCreateParams.builder().model(DEFAULT_MODEL).addDeveloperMessage(userContent)

        //         // OpenAiChatCompletionParameters params = new OpenAiChatCompletionParameters();
 
        //         ChatClient.Builder builder = ChatClient.builder(chatModel);
        //         ChatClient chatClient = builder.build();
        //         chatModel.call(null)

        //         // messages.stream()
        //         //                 .map(msg -> switch (msg.role()) {
        //         //                         case AIConstants.Roles.SYSTEM ->
        //         //                                 messageFactory.createSystemMessage(msg.message());

        //         //                         case AIConstants.Roles.USER ->
        //         //                                 // messageFactory.createUserMessage(msg.message());
        //         //                         case AIConstants.Roles.ASSISTANT ->
        //         //                                 // messageFactory.createAssistantMessage(msg.message());
        //         //                                 chatClient.prompt(msg.message());
        //         //                         default -> throw new BusinessException(AIConstants.Messages.UNEXPECTED_ROLE +
        //         //                                         msg.role());
        //         //                 })
        //         //                 .forEach(params::addMessages);
        //         // messages.forEach(record -> {
        //         // OpenAiChatMessage[] message = switch (record.role()) {
        //         // case AIConstants.Roles.SYSTEM ->
        //         // messageFactory.createSystemMessage(record.message());
        //         // case AIConstants.Roles.USER ->
        //         // messageFactory.createUserMessage(record.message());
        //         // case AIConstants.Roles.ASSISTANT ->
        //         // messageFactory.createAssistantMessage(record.message());
        //         // default -> throw new BusinessException(AIConstants.Messages.UNEXPECTED_ROLE +
        //         // record.role());
        //         // };
        //         // params.addMessages(message);
        //         // // messages.add(new CommonAIMessage(record.getRole(), record.getContent()));
        //         // });

        //         // OpenAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);
        //         // OpenAiChatCompletionOutput rawResult = aiClient.chatCompletion(params);

        //         // 使用适配器转换响应
        //         AIResponse aiResponse = aiResponseHandlerFactory
        //                         .getHandler(AIServiceType.SAP)
        //                         .processResponse(rawResult);

        //         aiResponseHelper.handleChatResponse(
        //                         aiResponse,
        //                         report,
        //                         context.getContent(),
        //                         entityInfo,
        //                         context);
        // }

        // @Override
        // public List<ReportFields> callAIforAdopt(ChatService readService, RecordsAdoptContext adoptContext,
        //                 Records originalRecord,
        //                 Reports report) {

        //         // TODO Auto-generated method stub
        //         // throw new UnsupportedOperationException("Unimplemented method
        //         // 'callAIforAdopt'");
        //         // Get AI function for JSON processing
        //         OpenAiChatCompletionFunction function = configUtils.getFunction(
        //                         readService,
        //                         adoptContext.getParameterInfo().getLocale(),
        //                         aiProperties.getFunctionForJson(),
        //                         OpenAiChatCompletionFunction.class);

        //         // Call AI to process record content
        //         OpenAiChatCompletionOutput rawResult = callAIWithFunction(
        //                         function,
        //                         originalRecord.getContent(),
        //                         "");

        //         // 使用适配器转换响应
        //         AIResponse aiResponse = aiResponseHandlerFactory
        //                         .getHandler(AIServiceType.SAP)
        //                         .processResponse(rawResult);

        //         List<ReportFields> fieldsList = new ArrayList<>();
        //         aiResponseHelper.processResponse(aiResponse, report, fieldsList, AIConstants.NodeKeys.FIELDS,
        //                         jsonUtils::createReportField);

        //         return fieldsList;

        // }

        // @Override
        // public List<Pcls> callAIforGeneratePCL(String fieldsInJSON, ChatService readService,
        //                 ReportsGeneratePCLContext generatePCLContext) {
        //         // TODO Auto-generated method stub
        //         // throw new UnsupportedOperationException("Unimplemented method
        //         // 'callAIforGeneratePCL'");
        //         AIParameters<OpenAiChatCompletionFunction> params = configUtils.getFunctionAndPrompt(
        //                         readService,
        //                         generatePCLContext.getParameterInfo().getLocale(),
        //                         aiProperties.getFunctionForPcl(),
        //                         aiProperties.getPromptPrefixForPcl(),
        //                         OpenAiChatCompletionFunction.class);

        //         // Call AI and process response
        //         OpenAiChatCompletionOutput rawResult = callAIWithFunction(
        //                         params.getFunction(),
        //                         fieldsInJSON,
        //                         params.getPromptContent());

        //         // 使用适配器转换响应
        //         AIResponse aiResponse = aiResponseHandlerFactory
        //                         .getHandler(AIServiceType.SAP)
        //                         .processResponse(rawResult);

        //         // Process PCLs
        //         List<Pcls> pclsList = new ArrayList<>();
        //         aiResponseHelper.processResponse(
        //                         aiResponse,
        //                         null,
        //                         pclsList,
        //                         "pcl", // Changed from AIConstants.NodeKeys.ITEMS to match function schema
        //                         jsonUtils::createPcl);
        //         return pclsList;
        // }

        // @Override
        // public void callAIforGenerateCDS(String fieldsInJSON, ChatService readService,
        //                 ReportsGenerateCDSContext generateCDSContext, Reports report) {
        //         // TODO Auto-generated method stub
        //         // throw new UnsupportedOperationException("Unimplemented method
        //         // 'callAIforGenerateCDS'");
        //         // Get AI parameters
        //         AIParameters<OpenAiChatCompletionFunction> params = configUtils.getFunctionAndPrompt(
        //                         readService,
        //                         generateCDSContext.getParameterInfo().getLocale(),
        //                         aiProperties.getFunctionForCds(),
        //                         aiProperties.getPromptPrefixForCds(),
        //                         OpenAiChatCompletionFunction.class);

        //         // Call AI service
        //         OpenAiChatCompletionOutput rawResult = callAIWithFunction(
        //                         params.getFunction(),
        //                         fieldsInJSON,
        //                         params.getPromptContent());

        //         // 使用适配器转换响应
        //         AIResponse aiResponse = aiResponseHandlerFactory
        //                         .getHandler(AIServiceType.SAP)
        //                         .processResponse(rawResult);

        //         // Process response and update CDS
        //         aiResponseHelper.handleCDSResponse(aiResponse, report);

        // }
}

// filepath:
// /D:/code/AI/ai2report_java/srv/src/main/java/customer/aireport/service/EntityService.java
