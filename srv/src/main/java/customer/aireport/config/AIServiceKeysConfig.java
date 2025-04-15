package customer.aireport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@ConfigurationProperties(prefix = "aiservice")
@Configuration
@Data
public class AIServiceKeysConfig {
    private String aiCoreDestination = "AICore"; // 默认值
    private String serviceType = "SAP"; // 默认值
    private String openAiApiKey;
    private Integer claudeMaxTokens = 4096; // Claude默认最大token数
}
