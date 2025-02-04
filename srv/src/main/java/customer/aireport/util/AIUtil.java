package customer.aireport.util;

import com.sap.ai.sdk.core.AiCoreService;
import com.sap.ai.sdk.foundationmodels.openai.OpenAiClient;
import com.sap.ai.sdk.foundationmodels.openai.OpenAiModel;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;

import javax.annotation.Nonnull;

public class AIUtil {

    public static OpenAiClient getAiClientbyModelUsingBTPDestination(@Nonnull OpenAiModel foundationModel) {

        // build api destination
        Destination destination = DestinationAccessor.getDestination("AICore");
        AiCoreService aiCoreService = new AiCoreService().withBaseDestination(destination.asHttp());
        // AiCoreService aiCoreService = new AiCoreService().withDestination(destination);

        // Destination destinationWithDeployment = aiCoreService.forDeploymentByModel(foundationModel)
        // aiCoreService.
        // aiCoreService.
        Destination destinationWithDeployment = aiCoreService.getInferenceDestination().forModel(foundationModel);
                // .withResourceGroup("default")
                // .destination();

        return OpenAiClient.withCustomDestination(destinationWithDeployment);
    }
}
