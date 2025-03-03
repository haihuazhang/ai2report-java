package customer.aireport.service;

import static com.sap.ai.sdk.foundationmodels.openai.OpenAiModel.GPT_4O;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.ai.sdk.core.AiCoreService;
import com.sap.ai.sdk.foundationmodels.openai.OpenAiClient;
import com.sap.ai.sdk.foundationmodels.openai.OpenAiModel;
import com.sap.ai.sdk.foundationmodels.openai.model.*;
// import static com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionToolType.FUNCTION;
import static com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionTool.ToolType;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;


import customer.aireport.config.AIProperties; // Changed from AIReportProperties
// import customer.aireport.util.AIUtil;

// Rename from AIUtil.java
@Service("openAIService") // Changed bean name to avoid conflict
public class AIService {
    private final OpenAiModel DEFAULT_MODEL = GPT_4O;
    private AIProperties aiProperties; // Changed from AIReportProperties

    @Autowired
    public void setAiProperties(AIProperties aiProperties) { // Changed method name and parameter type
        this.aiProperties = aiProperties; // Changed from AIUtil
    }

    public OpenAiClient getAiClientbyModelUsingBTPDestination(@Nonnull OpenAiModel foundationModel) {
        // build api destination
        Destination destination = DestinationAccessor.getDestination(aiProperties.getAiCoreDestination());
        AiCoreService aiCoreService = new AiCoreService().withBaseDestination(destination.asHttp());
        Destination destinationWithDeployment = aiCoreService.getInferenceDestination().forModel(foundationModel);
        return OpenAiClient.withCustomDestination(destinationWithDeployment);
    }

    public OpenAiChatCompletionOutput callAIWithFunction(
            OpenAiChatCompletionFunction function,
            String content) {
        return callAIWithFunction(function, content, "");
    }

    public OpenAiChatCompletionOutput callAIWithFunction(
            OpenAiChatCompletionFunction function,
            String content,
            String promptPrefix) {
        OpenAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);
        OpenAiChatCompletionTool tool = new OpenAiChatCompletionTool()
                .setType(ToolType.FUNCTION)
                .setFunction(function);

        OpenAiChatCompletionParameters params = new OpenAiChatCompletionParameters()
                .addMessages(
                        new OpenAiChatMessage.OpenAiChatUserMessage()
                                .addText(promptPrefix + content))
                .setTools(List.of(tool));

        return aiClient.chatCompletion(params);
    }

    public OpenAiChatCompletionOutput callAICompletion(
            OpenAiChatCompletionParameters params) {
        OpenAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);
        return aiClient.chatCompletion(params);
    }
}

// filepath:
// /D:/code/AI/ai2report_java/srv/src/main/java/customer/aireport/service/EntityService.java
