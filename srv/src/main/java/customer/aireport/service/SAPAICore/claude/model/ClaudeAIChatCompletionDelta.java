package customer.aireport.service.SAPAICore.claude.model;

import static lombok.AccessLevel.PACKAGE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.annotations.Beta;
import com.sap.ai.sdk.core.common.StreamedDelta;

import customer.aireport.service.SAPAICore.claude.generated.model.ConverseStreamResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Beta
@RequiredArgsConstructor(onConstructor_ = @JsonCreator, access = PACKAGE)
@Getter
@ToString
@EqualsAndHashCode
public class ClaudeAIChatCompletionDelta implements StreamedDelta {

    @Nonnull
    private final ConverseStreamResponse originalResponse;

    @Nonnull
    @Override
    public String getDeltaContent() {
        // return deltaContent;
        final var delta = getOriginalResponse().getContentBlockDelta();
        if (delta != null) {
            return delta.getDelta() == null ? "" : delta.getDelta().getText();
        }
        // return delta.getDelta().getText();
        return "";
    }

    @Nullable
    @Override
    public String getFinishReason() {
        // return finishReason;
        final var messageStop = getOriginalResponse().getMessageStop();
        if (messageStop != null) {
            return messageStop.getStopReason() != null ? messageStop.getStopReason().getValue() : null;
        }
        // getOriginalResponse().getMessageStop().getStopReason().getValue();
        return null;
    }

}
