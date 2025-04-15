package customer.aireport.adapter;

import customer.aireport.dto.AIResponse;
// ...other imports
import customer.aireport.service.SAPAICore.claude.generated.model.InvokeResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.InvokeResponseContentInner;
import customer.aireport.service.SAPAICore.claude.generated.model.ToolResponseContent;
import customer.aireport.service.SAPAICore.claude.generated.model.TextResponseContent;


public class SAPClaudeAIResponseAdapter implements AIResponse {
    private final InvokeResponse output;

    public SAPClaudeAIResponseAdapter(InvokeResponse output) {
        this.output = output;
    }

    @Override
    public String getContent() {
        if (output == null || output.getContent() == null || output.getContent().isEmpty()) {
            return "";
        }

        // Handle tool use case
        if (output.getStopReason() == InvokeResponse.StopReasonEnum.TOOL_USE) {
            StringBuilder toolContent = new StringBuilder();
            for (InvokeResponseContentInner content : output.getContent()) {
                if (content instanceof ToolResponseContent) {
                    ToolResponseContent toolResponse = (ToolResponseContent) content;
                    // Add tool name and input for better context
                    toolContent.append(toolResponse.getInput());
                }
                else if (content instanceof TextResponseContent) {
                    // TextResponseContent textResponse = (TextResponseContent) content;
                    // toolContent.append("\n").append(textResponse.getText());
                }
            }
            return toolContent.toString().trim();
        }

        // Handle normal chat completion case
        if (output.getStopReason() == InvokeResponse.StopReasonEnum.END_TURN || 
            output.getStopReason() == InvokeResponse.StopReasonEnum.STOP_SEQUENCE) {
            // For normal responses, we typically want the first text content
            InvokeResponseContentInner content = output.getContent().get(0);
            if (content instanceof TextResponseContent) {
                TextResponseContent textContent = (TextResponseContent) content;
                return textContent.getText();
            }
        }

        // Default case - try to get text content if available
        for (InvokeResponseContentInner content : output.getContent()) {
            if (content instanceof TextResponseContent) {
                return ((TextResponseContent) content).getText();
            }
        }

        // If we can't properly handle the content, return empty string
        return "";
    }
}