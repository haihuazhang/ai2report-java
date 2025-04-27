package customer.aireport.factory;

import customer.aireport.constant.AIServiceType;
import customer.aireport.handler.AIResponseHandler;
import customer.aireport.handler.SAPClaudeAIResponseHandler;
import customer.aireport.handler.SAPOpenAIResponseHandler;
import org.springframework.stereotype.Component;

@Component
public class AIResponseHandlerFactory {
    private final SAPOpenAIResponseHandler sapOpenAIHandler;
    private final SAPClaudeAIResponseHandler sapClaudeHandler;

    public AIResponseHandlerFactory(
            SAPOpenAIResponseHandler sapOpenAIHandler,
            SAPClaudeAIResponseHandler sapClaudeHandler) {
        this.sapOpenAIHandler = sapOpenAIHandler;
        this.sapClaudeHandler = sapClaudeHandler;
    }

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