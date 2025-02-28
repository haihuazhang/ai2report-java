package customer.aireport.util;

import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionFunction;
import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.ParameterItems;

public class ConfigurationUtil {
    public static ParameterItems[] getAIParameters(
            ChatService service, 
            AIReportProperties properties,
            String language,
            String... parameterNames) {
        ParameterItems[] items = new ParameterItems[parameterNames.length];
        for (int i = 0; i < parameterNames.length; i++) {
            items[i] = EntityServiceUtil.selectParameterItem(
                service,
                parameterNames[i],
                language,
                "Maintain_Parameter"
            );
        }
        return items;
    }

    public static AIParameters getParameters(ChatService service, 
            AIReportProperties properties,
            String locale, 
            String functionName, 
            String promptName) {
        ParameterItems[] items = getAIParameters(
            service, 
            properties, 
            locale, 
            functionName,
            promptName
        );
        
        OpenAiChatCompletionFunction function = JsonParseUtil.parseFunction(
            items[0].getValue(),
            "Error_When_Parsing_Parameter"
        );

        return new AIParameters(function, items[1].getValue());
    }

    @lombok.Value
    public static class AIParameters {
        OpenAiChatCompletionFunction function;
        String promptContent;
    }
}