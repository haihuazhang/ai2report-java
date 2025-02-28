package customer.aireport.util;

import com.sap.ai.sdk.core.AiCoreService;
import com.sap.ai.sdk.foundationmodels.openai.OpenAiClient;
import com.sap.ai.sdk.foundationmodels.openai.OpenAiModel;
import com.sap.ai.sdk.foundationmodels.openai.model.*;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.List;

import static com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionTool.ToolType.FUNCTION;
import static com.sap.ai.sdk.foundationmodels.openai.OpenAiModel.GPT_4O;

public class AIUtil {
    private static final OpenAiModel DEFAULT_MODEL = GPT_4O;
    private static AIReportProperties aiReportProperties;

    @Autowired
    public void setAiReportProperties(AIReportProperties aiReportProperties) {
        AIUtil.aiReportProperties = aiReportProperties;
    }

    public static OpenAiClient getAiClientbyModelUsingBTPDestination(@Nonnull OpenAiModel foundationModel) {
        // build api destination
        Destination destination = DestinationAccessor.getDestination(aiReportProperties.getAiCoreDestination());
        AiCoreService aiCoreService = new AiCoreService().withBaseDestination(destination.asHttp());
        Destination destinationWithDeployment = aiCoreService.getInferenceDestination().forModel(foundationModel);
        return OpenAiClient.withCustomDestination(destinationWithDeployment);
    }

    public static OpenAiChatCompletionOutput callAIWithFunction(
            OpenAiChatCompletionFunction function,
            String content) {
        return callAIWithFunction(function, content, "");
    }

    public static OpenAiChatCompletionOutput callAIWithFunction(
            OpenAiChatCompletionFunction function,
            String content,
            String promptPrefix) {
        OpenAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);
        OpenAiChatCompletionTool tool = new OpenAiChatCompletionTool()
            .setType(FUNCTION)
            .setFunction(function);

        OpenAiChatCompletionParameters params = new OpenAiChatCompletionParameters()
            .addMessages(
                new OpenAiChatMessage.OpenAiChatUserMessage()
                    .addText(promptPrefix + content)
            )
            .setTools(List.of(tool));

        return aiClient.chatCompletion(params);
    }

    public static OpenAiChatCompletionOutput callAICompletion(
            OpenAiChatCompletionParameters params) {
        OpenAiClient aiClient = getAiClientbyModelUsingBTPDestination(DEFAULT_MODEL);
        return aiClient.chatCompletion(params);
    }
}
