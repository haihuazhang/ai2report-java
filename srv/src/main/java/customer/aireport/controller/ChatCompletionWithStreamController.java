package customer.aireport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sap.ai.sdk.foundationmodels.openai.model.OpenAiUsage;

import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import customer.aireport.config.AIProperties;
import customer.aireport.constant.AIConstants;
import customer.aireport.helper.ChatHelper;
import customer.aireport.model.CommonAIMessage;
import customer.aireport.model.EntityInfo;
import customer.aireport.model.StreamChatRequest;
import customer.aireport.service.EntityService;
import customer.aireport.service.SAPAICore.SAPOpenAIService;
import customer.aireport.util.ConfigUtils;
import jakarta.servlet.http.HttpServletResponse;
import cds.gen.chatservice.ChatService;
import cds.gen.chatservice.Records;
import cds.gen.chatservice.Reports;

@RestController
@RequestMapping("/api/chat")
public class ChatCompletionWithStreamController {

    @Autowired
    private SAPOpenAIService sapOpenAIService;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ChatService aiService;

    @Autowired
    private ChatService.Draft aiServiceDraft;

    @Autowired
    private EntityService entityService;

    @Autowired
    private AIProperties aiProperties;

    @Autowired
    private ChatHelper chatHelper;

    /* 
     * Stream Chat Completion with Stream API
     * 
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE )
    public SseEmitter streamChatCompletion(@RequestBody StreamChatRequest request,HttpServletResponse response) {
        
        // Set Cache-Control header
        // To disable compression of the response by SAP Approuter
        response.setHeader("Cache-Control", "no-transform");

        List<Records> records = entityService.selectRecordsByReportId(aiService, request.getId(),
                request.getIsActiveEntity());
        Reports report = entityService.selectReportById(aiService,
                request.getId(), request.getIsActiveEntity(), AIConstants.Messages.REPORT_NOT_FOUND);

        // Get system prompt content
        String promptContent = configUtils.getPrompt(
                aiService,
                Locale.of(request.getLocale()),
                aiProperties.getOpenai().getPromptPrefixForReport());

        // Prepare chat parameters
        List<CommonAIMessage> commonAIMessages = new ArrayList<>();

        // Handle new or existing chat
        if (records.isEmpty()) {
            // Create new system record
            Records systemRecord = chatHelper.handleNewChat(
                    commonAIMessages,
                    promptContent,
                    request.getContent(),
                    request.getId(),
                    request.getIsActiveEntity());
            entityService.insertRecord(aiService, aiServiceDraft, systemRecord, request.getIsActiveEntity());
        } else {
            // Add existing chat history
            chatHelper.handleExistingChat(request.getContent(), commonAIMessages, records);
        }

        return sapOpenAIService.callAIforStream(
                commonAIMessages,
                report, request);

    }

    @GetMapping(value = "/test-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter testStream() {
        SseEmitter emitter = new SseEmitter();

        // executor.execute(() -> {
        // try {
        // for (int i = 0; i < 1000; i++) {
        // send(emitter, "Test message " + i);
        // Thread.sleep(200);
        // }
        // } catch (InterruptedException e) {
        // emitter.completeWithError(e);
        // } finally {
        // emitter.complete();
        // }
        // });

        return emitter;
    }

}