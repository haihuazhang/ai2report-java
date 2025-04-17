package customer.aireport.adapter;

import customer.aireport.dto.AIResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.InvokeResponse;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseRequestAssistantMessage;
import customer.aireport.service.SAPAICore.claude.generated.model.ConverseResponse;

import java.util.List;
import java.util.Map;

public class SAPClaudeAIResponseAdapter implements AIResponse {
    private final String content;
    // private final String stopReason;

    public SAPClaudeAIResponseAdapter(InvokeResponse response) {
        this.content = extractContentFromInvokeResponse(response);
        // this.stopReason = response.getStopReason().getValue();
    }

    public SAPClaudeAIResponseAdapter(ConverseResponse response) {
        this.content = extractContentFromConverseResponse(response);
        // this.stopReason = response.getStopReason().getValue();
    }

    private String extractContentFromInvokeResponse(InvokeResponse response) {
        if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
            return "";
        }

        // Handle tool use case
        if (response.getStopReason() == InvokeResponse.StopReasonEnum.TOOL_USE) {
            StringBuilder toolContent = new StringBuilder();
            for (var content : response.getContent()) {
                if (content instanceof customer.aireport.service.SAPAICore.claude.generated.model.ToolResponseContent) {
                    var toolResponse = (customer.aireport.service.SAPAICore.claude.generated.model.ToolResponseContent) content;
                    toolContent.append(toolResponse.getInput());
                }
            }
            return toolContent.toString().trim();
        }

        // Handle normal chat completion case
        if (response.getStopReason() == InvokeResponse.StopReasonEnum.END_TURN || 
            response.getStopReason() == InvokeResponse.StopReasonEnum.STOP_SEQUENCE) {
            var content = response.getContent().get(0);
            if (content instanceof customer.aireport.service.SAPAICore.claude.generated.model.TextResponseContent) {
                var textContent = (customer.aireport.service.SAPAICore.claude.generated.model.TextResponseContent) content;
                return textContent.getText();
            }
        }

        // Default case - try to get text content if available
        for (var content : response.getContent()) {
            if (content instanceof customer.aireport.service.SAPAICore.claude.generated.model.TextResponseContent) {
                return ((customer.aireport.service.SAPAICore.claude.generated.model.TextResponseContent) content).getText();
            }
        }

        // If we can't properly handle the content, return empty string
        return "";
    }

    private String extractContentFromConverseResponse(ConverseResponse response) {
        if (response == null) {
            return "";
        }

        // Handle tool use case
        if (ConverseResponse.StopReasonEnum.TOOL_USE.equals(response.getStopReason())) {
            StringBuilder toolContent = new StringBuilder();
            ConverseRequestAssistantMessage assistantMessage = (ConverseRequestAssistantMessage) response.getOutput().getMessage();

            for (var content : assistantMessage.getContent()) {
                toolContent.append(content.getToolUse().getInput());
                // if (content instanceof customer.aireport.service.SAPAICore.claude.generated.model.ToolResponseContent) {
                //     var toolResponse = (customer.aireport.service.SAPAICore.claude.generated.model.ToolResponseContent) content;
                //     toolContent.append(toolResponse.getInput());
                // }
            }
            return toolContent.toString().trim();
            // if (response.getOutput() instanceof Map) {
            //     @SuppressWarnings("unchecked")
            //     Map<String, Object> output = (Map<String, Object>) response.getOutput();
            //     if (output.containsKey("tool_calls")) {
            //         @SuppressWarnings("unchecked")
            //         List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) output.get("tool_calls");
            //         StringBuilder toolContent = new StringBuilder();
            //         for (Map<String, Object> toolCall : toolCalls) {
            //             if (toolCall.containsKey("function")) {
            //                 Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
            //                 if (function.containsKey("arguments")) {
            //                     toolContent.append(function.get("arguments"));
            //                 }
            //             }
            //         }
            //         return toolContent.toString().trim();
            //     }
            // }
        }

        // Handle normal chat completion case
        if (ConverseResponse.StopReasonEnum.STOP_SEQUENCE.equals(response.getStopReason()) || 
            ConverseResponse.StopReasonEnum.MAX_TOKENS.equals(response.getStopReason()) || 
            ConverseResponse.StopReasonEnum.CONTENT_FILTERED.equals(response.getStopReason())) {
            // if (response.getOutput() instanceof Map) {
            //     @SuppressWarnings("unchecked")
            //     Map<String, Object> output = (Map<String, Object>) response.getOutput();
            //     if (output.containsKey("content")) {
            //         return output.get("content").toString();
            //     }
            // }
            StringBuilder toolContent = new StringBuilder();
            ConverseRequestAssistantMessage assistantMessage = (ConverseRequestAssistantMessage) response.getOutput().getMessage();
            for (var content : assistantMessage.getContent()) {
                toolContent.append(content.getText());
            }
            return toolContent.toString().trim();
        }

        // Default case - try to get any content from output
        if (response.getOutput() != null) {
            return response.getOutput().toString();
        }

        return "";
    }

    @Override
    public String getContent() {
        return content;
    }

    // @Override
    // public String getStopReason() {
    //     return stopReason;
    // }
}