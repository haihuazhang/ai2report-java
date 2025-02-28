package customer.aireport.util;

import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.AnalysisResult;
import com.sap.cds.reflect.CdsModel;
import java.util.Locale;
import java.util.Map;

public class RequestAnalyzeUtil {
    
    public static class EntityInfo {
        private final String id;
        private final Boolean isActiveEntity;

        public EntityInfo(String id, Boolean isActiveEntity) {
            this.id = id;
            this.isActiveEntity = isActiveEntity;
        }

        public String getId() {
            return id;
        }

        public Boolean getIsActiveEntity() {
            return isActiveEntity;
        }
    }

    public static EntityInfo analyzeRequest(CqnSelect select, CdsModel model) {
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(model);
        AnalysisResult analysisResult = cqnAnalyzer.analyze(select);
        Map<String, Object> rootKeys = analysisResult.rootKeys();
        
        String id = (String) rootKeys.get("ID");
        Boolean isActiveEntity = (Boolean) rootKeys.get("IsActiveEntity");
        
        return new EntityInfo(id, isActiveEntity);
    }

    public static String getLocaleString(Locale locale) {
        if (locale == null) {
            locale = Locale.of("zh");
        }
        return locale.getLanguage();
    }
}