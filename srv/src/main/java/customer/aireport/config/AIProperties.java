package customer.aireport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

// Rename from AIReportProperties.java
@ConfigurationProperties(prefix = "aireport")
@Configuration
@Data
public class AIProperties {
    // ...existing code...
    private String promptPrefixForReport;
    private String promptPrefixForReportName;
    private String functionForJson;
    private String promptPrefixForPcl;
    private String functionForPcl;
    private String aiCoreDestination = "AICore"; // 默认值
}