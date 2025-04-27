package customer.aireport.resolver;

import customer.aireport.service.AIService.AIServiceI;
import customer.aireport.config.AIServiceKeysConfig;
import customer.aireport.constant.AIServiceType;

public class AIServiceResolver {
    private final AIServiceI sapOpenAIService;
    private final AIServiceI sapClaudeAIService;
    private final AIServiceKeysConfig aiServiceKeys;

    public AIServiceResolver(
            AIServiceI sapOpenAIService,
            AIServiceI sapClaudeAIService,
            AIServiceKeysConfig aiServiceKeys) {
        this.sapOpenAIService = sapOpenAIService;
        this.sapClaudeAIService = sapClaudeAIService;
        this.aiServiceKeys = aiServiceKeys;
    }

    public AIServiceI getActiveAIService() {
        AIServiceType type = AIServiceType.valueOf(aiServiceKeys.getServiceType().toUpperCase());
        return switch (type) {
            case SAPOPENAI -> sapOpenAIService;
            case SAPCLAUDE -> sapClaudeAIService;
            default -> throw new IllegalStateException("Unsupported AI service type: " + type);
        };
    }

    public AIServiceI getSAPAiService() {
        return sapOpenAIService;
    }
    public AIServiceI getSAPClaudeAiService() {
        return sapClaudeAIService;
    }
}