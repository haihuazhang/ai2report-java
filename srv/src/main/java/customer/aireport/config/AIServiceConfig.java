package customer.aireport.config;

import customer.aireport.service.SAPAICore.SAPClaudeAIService;
import customer.aireport.service.SAPAICore.SAPOpenAIService;
import customer.aireport.service.AIService.AIServiceI;
import customer.aireport.constant.AIServiceType;
import customer.aireport.resolver.AIServiceResolver;
import customer.aireport.factory.AIResponseHandlerFactory;
import customer.aireport.factory.SAPClaudeAIMessageFactory;
import customer.aireport.factory.SAPOpenAIMessageFactory;
import customer.aireport.helper.AIResponseHelper;
import customer.aireport.util.ConfigUtils;
import customer.aireport.util.JsonUtils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIServiceConfig {

    @Bean
    @Primary
    @Qualifier("sapOpenAIService")
    public SAPOpenAIService sapOpenAIService(
            AIProperties aiProperties,
            AIServiceKeysConfig aiServiceKeys,
            SAPOpenAIMessageFactory messageFactory,
            AIResponseHelper aiResponseHelper,
            ConfigUtils configUtils,
            JsonUtils jsonUtils,
            AIResponseHandlerFactory aiResponseHandlerFactory) {
        return new SAPOpenAIService(
                aiProperties,
                aiServiceKeys,
                messageFactory,
                aiResponseHelper,
                configUtils,
                jsonUtils,
                aiResponseHandlerFactory);
    }

    @Bean
    @Primary
    @Qualifier("sapClaudeAIService")
    public SAPClaudeAIService sapClaudeAIService(
            AIProperties aiProperties,
            AIServiceKeysConfig aiServiceKeys,
            SAPClaudeAIMessageFactory messageFactory,
            AIResponseHelper aiResponseHelper,
            ConfigUtils configUtils,
            JsonUtils jsonUtils,
            AIResponseHandlerFactory aiResponseHandlerFactory) {
        return new SAPClaudeAIService(
                aiProperties,
                aiServiceKeys,
                messageFactory,
                aiResponseHelper,
                configUtils,
                jsonUtils,
                aiResponseHandlerFactory);
    }

    @Bean
    public AIServiceResolver aiServiceResolver(
            @Qualifier("sapOpenAIService") AIServiceI sapOpenAIService,
            @Qualifier("sapClaudeAIService") AIServiceI sapClaudeAIService,
            AIServiceKeysConfig aiServiceKeys) {
        return new AIServiceResolver(sapOpenAIService, sapClaudeAIService, aiServiceKeys);
    }
}