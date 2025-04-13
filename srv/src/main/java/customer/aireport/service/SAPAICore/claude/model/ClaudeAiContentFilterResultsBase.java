package customer.aireport.service.SAPAICore.claude.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/** Information about the content filtering results. */
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@Beta
public class ClaudeAiContentFilterResultsBase {
  /** Sexual content filter result. */
  @JsonProperty("sexual")
  @Getter(onMethod_ = @Nullable)
  private ClaudeAiContentFilterSeverityResult sexual;

  /** Violent content filter result. */
  @JsonProperty("violence")
  @Getter(onMethod_ = @Nullable)
  private ClaudeAiContentFilterSeverityResult violence;

  /** Hate speech content filter result. */
  @JsonProperty("hate")
  @Getter(onMethod_ = @Nullable)
  private ClaudeAiContentFilterSeverityResult hate;

  /** Intolerant content filter result. */
  @JsonProperty("self_harm")
  @Getter(onMethod_ = @Nullable)
  private ClaudeAiContentFilterSeverityResult selfHarm;

  /** Profanity content filter result. */
  @JsonProperty("profanity")
  @Getter(onMethod_ = @Nullable)
  private ClaudeAiContentFilterDetectedResult profanity;

  @JsonProperty("error")
  @Getter(onMethod_ = @Nullable)
  private ClaudeAiErrorBase error;

  void addDelta(@Nonnull final ClaudeAiContentFilterPromptResults delta) {
    if (delta.getSexual() != null) {
      sexual = delta.getSexual();
    }
    if (delta.getViolence() != null) {
      violence = delta.getViolence();
    }
    if (delta.getHate() != null) {
      hate = delta.getHate();
    }
    if (delta.getSelfHarm() != null) {
      selfHarm = delta.getSelfHarm();
    }
    if (delta.getProfanity() != null) {
      profanity = delta.getProfanity();
    }
    if (delta.getError() != null) {
      error = delta.getError();
    }
  }
}
