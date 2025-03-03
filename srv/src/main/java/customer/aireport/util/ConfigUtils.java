package customer.aireport.util;

import org.springframework.stereotype.Component;
import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiChatCompletionFunction;
import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.ParameterItems;
import customer.aireport.config.AIProperties;
import customer.aireport.model.AIParameters;
import customer.aireport.service.EntityService;

@Component
public class ConfigUtils {
    private final EntityService entityService;
    private final JsonUtils jsonUtils;

    public ConfigUtils(EntityService entityService, JsonUtils jsonUtils) {
        this.entityService = entityService;
        this.jsonUtils = jsonUtils;
    }

    // For cases that need both function and prompt
    public AIParameters getFunctionAndPrompt(
            ChatService service, 
            AIProperties properties,
            String locale, 
            String functionName, 
            String promptName) {
        ParameterItems functionItem = getParameter(service, functionName, locale);
        ParameterItems promptItem = getParameter(service, promptName, locale);
        
        OpenAiChatCompletionFunction function = jsonUtils.parseFunction(
            functionItem.getValue(),
            "Error_When_Parsing_Parameter"
        );

        return new AIParameters(function, promptItem.getValue());
    }

    // For cases that only need prompt
    public String getPrompt(
            ChatService service, 
            String locale, 
            String promptName) {
        ParameterItems item = getParameter(service, promptName, locale);
        return item.getValue();
    }

    // For cases that only need function
    public OpenAiChatCompletionFunction getFunction(
            ChatService service, 
            String locale, 
            String functionName) {
        ParameterItems item = getParameter(service, functionName, locale);
        return jsonUtils.parseFunction(
            item.getValue(),
            "Error_When_Parsing_Parameter"
        );
    }

    private ParameterItems getParameter(
            ChatService service,
            String parameterName,
            String language) {
        return entityService.selectParameterItem(
            service,
            parameterName,
            language,
            "Maintain_Parameter"
        );
    }
}

