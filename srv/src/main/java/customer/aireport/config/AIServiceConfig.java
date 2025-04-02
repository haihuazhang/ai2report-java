package customer.aireport.config;

import customer.aireport.service.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIServiceConfig {

    @Bean
    @Primary
    @Qualifier("sapAIService")
    public SAPAIService sapAIService() {
        // Your existing SAP AIService implementation
        return new SAPAIService();
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