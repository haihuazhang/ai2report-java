package customer.aireport.factory;

import customer.aireport.constant.AIServiceType;
// import static customer.aireport.constant.AIServiceType.SAP;
import customer.aireport.handler.AIResponseHandler;
import customer.aireport.handler.SAPClaudeAIResponseHandler;
import customer.aireport.handler.SAPOpenAIResponseHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AIResponseHandlerFactory {
    @Autowired
    private SAPOpenAIResponseHandler sapOpenAIHandler;

    @Autowired
    private SAPClaudeAIResponseHandler sapClaudeHandler;

    // public AIResponseHandlerFactory(SAPOpenAIResponseHandler sapOpenAIHandler) {
    //     this.sapOpenAIHandler = sapOpenAIHandler;
    // }

    public AIResponseHandler getHandler(AIServiceType type) {
        switch (type) {
            case SAPOPENAI:
                return sapOpenAIHandler;
            case SAPCLAUDE:
                return sapClaudeHandler;
            default:
                throw new UnsupportedOperationException("Unsupported AI service type: " + type);
        }
    }
}