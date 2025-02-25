package customer.aireport.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@ConfigurationProperties("aireport")
@Configuration
@Data
public class AIReportProperties {
    private String promptPrefixForReport;
    private String promptPrefixForReportName;
    private String promptPrefixForJson;
    private String promptPrefixForPCL;
    private String functionForPCL; 
}
