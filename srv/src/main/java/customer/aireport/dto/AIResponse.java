package customer.aireport.dto;

import java.util.List;

public interface AIResponse {
    String getFinishReason();

    String getContent();

    List<AIToolCall> getToolCalls();
}