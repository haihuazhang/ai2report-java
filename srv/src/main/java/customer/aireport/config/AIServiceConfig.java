package customer.aireport.config;

import customer.aireport.service.SAPAICore.SAPClaudeAIService;
import customer.aireport.service.SAPAICore.SAPOpenAIService;

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

    // @Bean
    // @Qualifier("directOpenAIService")
    // public AIService directOpenAIService() {
    //     // Your DirectOpenAIService implementation
    //     return new DirectOpenAIService();
    // }

    // @Bean
    // @Qualifier("deepSeekService")
    // public AIService deepSeekService() {
    //     // DeepSeek implementation
    //     return new DeepSeekService();
    // }
}