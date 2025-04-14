package customer.aireport.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.ParameterItems;
import customer.aireport.config.AIServiceKeysConfig;
import customer.aireport.constant.AIServiceType;
import customer.aireport.model.AIParameters;
import customer.aireport.service.EntityService;
import customer.aireport.service.AIService.AIServiceI;

@Component
public class ConfigUtils {
    private final EntityService entityService;
    private final JsonUtils jsonUtils;
    private final RequestUtils requestUtils;
    private final AIServiceKeysConfig aiServiceKeys;

    @Autowired
    @Qualifier("sapOpenAIService")
    private AIServiceI sapOpenAIService;

    @Autowired
    @Qualifier("sapClaudeAIService")
    private AIServiceI sapClaudeAIService;

    public ConfigUtils(EntityService entityService, JsonUtils jsonUtils, RequestUtils requestUtils,
            AIServiceKeysConfig aiServiceKeys) {
        this.entityService = entityService;
        this.jsonUtils = jsonUtils;
        this.requestUtils = requestUtils;
        this.aiServiceKeys = aiServiceKeys;
    }

    public AIServiceType getAIServiceType() {
        return AIServiceType.valueOf(aiServiceKeys.getServiceType().toUpperCase());
    }

    /**
     * Returns the active AI service based on configuration
     * @return The configured AI service implementation
     */
    public AIServiceI getActiveAIService() {
        return switch (getAIServiceType()) {
            case SAPOPENAI -> sapOpenAIService;
            case SAPCLAUDE -> sapClaudeAIService;
            default -> throw new IllegalStateException("Unsupported AI service type: " + getAIServiceType());
        };
    }

    // For cases that need both function and prompt
    public <T> AIParameters<T> getFunctionAndPrompt(
            ChatService service,
            Locale locale,
            String functionName,
            String promptName,
            Class<T> functionClass) {
        String language = requestUtils.getLocaleString(locale);
        ParameterItems functionItem = getParameter(service, functionName, language);
        ParameterItems promptItem = getParameter(service, promptName, language);

        T function = jsonUtils.parseFunction(
                functionItem.getValue(),
                "Error_When_Parsing_Parameter",
                functionClass);

        return new AIParameters<>(function, promptItem.getValue());
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
    public <T> T getFunction(
            ChatService service,
            Locale locale,
            String functionName,
            Class<T> clazz) {
        String language = requestUtils.getLocaleString(locale);
        ParameterItems item = getParameter(service, functionName, language);
        return jsonUtils.parseFunction(
                item.getValue(),
                "Error_When_Parsing_Parameter",
                clazz);
    }

    private ParameterItems getParameter(
            ChatService service,
            String parameterName,
            String language) {
        return entityService.selectParameterItem(
                service,
                parameterName,
                language,
                "Maintain_Parameter");
    }
}
