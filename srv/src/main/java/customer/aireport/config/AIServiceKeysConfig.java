package customer.aireport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@ConfigurationProperties(prefix = "aiService")
@Configuration
@Data
public class AIServiceKeysConfig {
    private String aiCoreDestination = "AICore"; // 默认值
    private String serviceType = "SAP"; // 默认值
    private String openAiApiKey;
}
