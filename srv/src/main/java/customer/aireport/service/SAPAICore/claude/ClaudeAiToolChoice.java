package customer.aireport.service.SAPAICore.claude;

import com.google.common.annotations.Beta;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionNamedToolChoice;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionNamedToolChoiceFunction;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionToolChoiceOption;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * ClaudeAi ToolChoice to specify whether to call which tool.
 *
 * @since 1.4.0
 */
@Beta
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaudeAiToolChoice {
  @Nonnull final ChatCompletionToolChoiceOption toolChoice;

  /** Only message generation will be performed without calling any tool. */
  public static final ClaudeAiToolChoice NONE =
      new ClaudeAiToolChoice(ChatCompletionToolChoiceOption.create("none"));

  /** The model may decide whether to call a (one or more) tool. */
  public static final ClaudeAiToolChoice OPTIONAL =
      new ClaudeAiToolChoice(ChatCompletionToolChoiceOption.create("auto"));

  /** The model must call one or more tools as part of its processing. */
  public static final ClaudeAiToolChoice REQUIRED =
      new ClaudeAiToolChoice(ChatCompletionToolChoiceOption.create("required"));

  /**
   * The model must call the function specified by {@code functionName}.
   *
   * @param functionName the name of the function that must be called.
   * @return the OpenAI tool choice.
   */
  @Nonnull
  public static ClaudeAiToolChoice function(@Nonnull final String functionName) {
    return new ClaudeAiToolChoice(
        ChatCompletionToolChoiceOption.create(
            new ChatCompletionNamedToolChoice()
                .type(ChatCompletionNamedToolChoice.TypeEnum.FUNCTION)
                .function(new ChatCompletionNamedToolChoiceFunction().name(functionName))));
  }
}
