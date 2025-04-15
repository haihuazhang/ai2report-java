package customer.aireport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@ConfigurationProperties(prefix = "aireport")
@Configuration
@Data
public class AIProperties {
    private OpenAIConfig openai = new OpenAIConfig();
    private ClaudeAIConfig claude = new ClaudeAIConfig();
    
    @Data
    public  class OpenAIConfig {
        private String promptPrefixForReport;
        private String promptPrefixForReportName;
        private String functionForJson;
        private String promptPrefixForPcl;
        private String functionForPcl;
        private String functionForCds;
        private String promptPrefixForCds;
    }
    
    @Data
    public class ClaudeAIConfig {
        private String promptPrefixForReport;
        private String promptPrefixForReportName;
        private String functionForJson;
        private String promptPrefixForPcl;
        private String functionForPcl;
        private String functionForCds;
        private String promptPrefixForCds;
    }
}