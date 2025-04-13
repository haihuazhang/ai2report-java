package customer.aireport.factory;

import customer.aireport.constant.AIServiceType;
// import static customer.aireport.constant.AIServiceType.SAP;
import customer.aireport.handler.AIResponseHandler;
import customer.aireport.handler.SAPOpenAIResponseHandler;
import org.springframework.stereotype.Component;

@Component
public class AIResponseHandlerFactory {
    private final SAPOpenAIResponseHandler sapOpenAIHandler;

    public AIResponseHandlerFactory(SAPOpenAIResponseHandler sapOpenAIHandler) {
        this.sapOpenAIHandler = sapOpenAIHandler;
    }

    public AIResponseHandler getHandler(AIServiceType type) {
        switch (type) {
            case SAP:
                return sapOpenAIHandler;
            case SAPCLAUDE:
                return sapOpenAIHandler;
            default:
                throw new UnsupportedOperationException("Unsupported AI service type: " + type);
        }
    }
}