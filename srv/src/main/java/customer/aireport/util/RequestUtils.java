package customer.aireport.util;

import org.springframework.stereotype.Component;
import java.util.Locale;
import java.util.Map;
import com.sap.cds.ql.cqn.*;
import com.sap.cds.reflect.CdsModel;
import customer.aireport.model.EntityInfo;

@Component
public class RequestUtils {
    
    public EntityInfo analyzeRequest(CqnSelect select, CdsModel model) {
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(model);
        AnalysisResult analysisResult = cqnAnalyzer.analyze(select);
        Map<String, Object> rootKeys = analysisResult.rootKeys();
        
        String id = (String) rootKeys.get("ID");
        Boolean isActiveEntity = (Boolean) rootKeys.get("IsActiveEntity");
        
        return new EntityInfo(id, isActiveEntity);
    }

    public String getLocaleString(Locale locale) {
        if (locale == null) {
            locale = Locale.of("zh");
        }
        return locale.getLanguage();
    }
}