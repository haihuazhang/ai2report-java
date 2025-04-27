package customer.aireport.adapter;

import customer.aireport.dto.AIResponse;

public class SAPOpenAIStreamResponseAdapter implements AIResponse {
    private final String content;

    public SAPOpenAIStreamResponseAdapter(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}
