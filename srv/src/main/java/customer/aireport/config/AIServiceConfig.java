package customer.aireport.config;

import customer.aireport.service.SAPAICore.SAPClaudeAIService;
import customer.aireport.service.SAPAICore.SAPOpenAIService;
import customer.aireport.service.AIService.AIServiceI;
import customer.aireport.constant.AIServiceType;
import customer.aireport.resolver.AIServiceResolver;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIServiceConfig {

    @Bean
    @Primary
    @Qualifier("sapOpenAIService")
    public SAPOpenAIService sapOpenAIService() {
        // Your existing SAP AIService implementation
        return new SAPOpenAIService();
    }

    @Bean
    @Primary
    @Qualifier("sapClaudeAIService")
    public SAPClaudeAIService sapClaudeAIService() {
        // Your existing SAP AIService implementation
        return new SAPClaudeAIService();
    }

    @Bean
    public AIServiceResolver aiServiceResolver(
            @Qualifier("sapOpenAIService") AIServiceI sapOpenAIService,
            @Qualifier("sapClaudeAIService") AIServiceI sapClaudeAIService,
            AIServiceKeysConfig aiServiceKeys) {
        return new AIServiceResolver(sapOpenAIService, sapClaudeAIService, aiServiceKeys);
    }

    // @Bean
    // @Qualifier("directOpenAIService")
    // public AIService directOpenAIService() {
    // // Your DirectOpenAIService implementation
    // return new DirectOpenAIService();
    // }

    // @Bean
    // @Qualifier("deepSeekService")
    // public AIService deepSeekService() {
    // // DeepSeek implementation
    // return new DeepSeekService();
    // }
}