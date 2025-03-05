package customer.aireport.util;

import java.util.Locale;

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
    private final RequestUtils requestUtils;

    public ConfigUtils(EntityService entityService, JsonUtils jsonUtils, RequestUtils requestUtils) {
        this.entityService = entityService;
        this.jsonUtils = jsonUtils;
        this.requestUtils = requestUtils;
    }

    // For cases that need both function and prompt
    public AIParameters getFunctionAndPrompt(
            ChatService service, 
            AIProperties properties,
            Locale locale, 
            String functionName, 
            String promptName) {
        String language = requestUtils.getLocaleString(locale);
        ParameterItems functionItem = getParameter(service, functionName, language);
        ParameterItems promptItem = getParameter(service, promptName, language);
        
        OpenAiChatCompletionFunction function = jsonUtils.parseFunction(
            functionItem.getValue(),
            "Error_When_Parsing_Parameter"
        );

        return new AIParameters(function, promptItem.getValue());
    }

    // For cases that only need prompt
    public String getPrompt(
            ChatService service, 
            Locale locale, 
            String promptName) {
        String language = requestUtils.getLocaleString(locale);
        return getParameter(service, promptName, language).getValue();
    }

    // For cases that only need function
    public OpenAiChatCompletionFunction getFunction(
            ChatService service, 
            Locale locale, 
            String functionName) {
        String language = requestUtils.getLocaleString(locale);
        ParameterItems item = getParameter(service, functionName, language);
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

