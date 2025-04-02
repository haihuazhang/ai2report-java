package customer.aireport.handler;

import customer.aireport.dto.AIResponse;

public interface AIResponseHandler {
    AIResponse processResponse(Object rawResponse);
}