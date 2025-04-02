package customer.aireport.factory;

import customer.aireport.constant.AIServiceType;
// import static customer.aireport.constant.AIServiceType.SAP;
import customer.aireport.handler.AIResponseHandler;
import customer.aireport.handler.SAPAIResponseHandler;
import org.springframework.stereotype.Component;

@Component
public class AIResponseHandlerFactory {
    private final SAPAIResponseHandler sapAIHandler;

    public AIResponseHandlerFactory(SAPAIResponseHandler sapAIHandler) {
        this.sapAIHandler = sapAIHandler;
    }

    public AIResponseHandler getHandler(AIServiceType type) {
        switch (type) {
            case SAP:
                return sapAIHandler;
            default:
                throw new UnsupportedOperationException("Unsupported AI service type: " + type);
        }
    }
}