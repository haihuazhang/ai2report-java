package customer.aireport.service.SAPAICore.claude;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionStreamOptions;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionTool;
import customer.aireport.service.SAPAICore.claude.generated.model.ChatCompletionToolChoiceOption;
import customer.aireport.service.SAPAICore.claude.generated.model.CreateChatCompletionRequest;
import customer.aireport.service.SAPAICore.claude.generated.model.CreateChatCompletionRequestAllOfResponseFormat;
import customer.aireport.service.SAPAICore.claude.generated.model.CreateChatCompletionRequestAllOfStop;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.With;
import lombok.experimental.Tolerate;

/**
 * Represents a request for OpenAI chat completion, including conversation
 * messages and parameters.
 *
 * @see <a
 *      href=
 *      "https://platform.openai.com/docs/api-reference/chat/create#chat-create-messages">OpenAI
 *      API Reference</a>
 * @since 1.4.0
 */
@Beta
@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.NONE)
public class ClaudeAiChatCompletionRequest {

  @Nonnull
  String anthropicVersion;

  /** List of messages from the conversation. */
  @Nonnull
  List<ClaudeAiMessage> messages;

  @Nullable
  String system;

  /**
   * Upto 4 Stop sequences to interrupts token generation and returns a response
   * without them.
   */
  @Nullable
  List<String> stopSequences;

  /**
   * Controls the randomness of the completion.
   *
   * <p>
   * Lower values (e.g. 0.0) make the model more deterministic and repetitive,
   * while higher
   * values (e.g. 1.0) make the model more random and creative.
   */
  @Nullable
  BigDecimal temperature;

  /**
   * Controls the cumulative probability threshold used for nucleus sampling.
   * Alternative to {@link
   * #temperature}.
   *
   * <p>
   * Lower values (e.g. 0.1) limit the model to consider only the smallest set of
   * tokens whose
   * combined probabilities add up to at least 10% of the total.
   */
  @Nullable
  BigDecimal topP;

  /** Maximum number of tokens that can be generated for the completion. */
  @Nullable
  Integer maxTokens;


  /** List of tools that the model may invoke during the completion. */
  @Nullable
  List<ChatCompletionTool> tools;

  /** Option to control which tool is invoked by the model. */
  @With(AccessLevel.PRIVATE)
  @Nullable
  ChatCompletionToolChoiceOption toolChoice;

  /**
   * Creates an ClaudeAiChatCompletionPrompt with string as user message.
   *
   * @param message the message to be added to the prompt
   */
  @Tolerate
  public ClaudeAiChatCompletionRequest(@Nonnull final String message) {
    this(ClaudeAiMessage.user(message));
  }

  /**
   * Creates an ClaudeAiChatCompletionPrompt with a multiple unpacked messages.
   *
   * @param message  the primary message to be added to the prompt
   * @param messages additional messages to be added to the prompt
   */
  @Tolerate
  public ClaudeAiChatCompletionRequest(
      @Nonnull final ClaudeAiMessage message, @Nonnull final ClaudeAiMessage... messages) {
    this(Lists.asList(message, messages));
  }

  /**
   * Creates an ClaudeAiChatCompletionPrompt with a list of messages.
   *
   * @param messages the list of messages to be added to the prompt
   * @since 1.6.0
   */
  @Tolerate
  public ClaudeAiChatCompletionRequest(@Nonnull final List<ClaudeAiMessage> messages) {
    this(
        null,
        List.copyOf(messages),
        null,
        null,
        null,
        null, null, null, null);
  }

  // /**
  // * Adds stop sequences to the request.
  // *
  // * @param sequence the primary stop sequence
  // * @param sequences additional stop sequences
  // * @return a new ClaudeAiChatCompletionRequest instance with the specified
  // stop sequences
  // */
  // @Tolerate
  // @Nonnull
  // public ClaudeAiChatCompletionRequest withStop(
  // @Nonnull final String sequence, @Nonnull final String... sequences) {
  // return this.withStop(Lists.asList(sequence, sequences));
  // }

  // /**
  // * Sets the parallel tool calls option.
  // *
  // * @param parallelToolCalls Whether to allow parallel tool calls.
  // * @return A new instance with the specified option.
  // */
  // @Nonnull
  // public ClaudeAiChatCompletionRequest withParallelToolCalls(
  // @Nonnull final Boolean parallelToolCalls) {
  // return Objects.equals(this.parallelToolCalls, parallelToolCalls)
  // ? this
  // : new ClaudeAiChatCompletionRequest(
  // this.messages,
  // // this.stop,
  // this.temperature,
  // this.topP,
  // this.maxTokens,
  // // this.maxCompletionTokens,
  // // this.presencePenalty,
  // // this.frequencyPenalty,
  // // this.logitBias,
  // // this.user,
  // // this.logprobs,
  // // this.topLogprobs,
  // // this.n,
  // // parallelToolCalls,
  // this.seed,
  // this.streamOptions,
  // this.responseFormat,
  // this.tools,
  // this.toolChoice);
  // }

  // /**
  // * Sets the log probabilities option.
  // *
  // * @param logprobs Whether to include log probabilities in the response.
  // * @return A new instance with the specified option.
  // */
  // @Nonnull
  // public ClaudeAiChatCompletionRequest withLogprobs(@Nonnull final Boolean
  // logprobs) {
  // return Objects.equals(this.logprobs, logprobs)
  // ? this
  // : new ClaudeAiChatCompletionRequest(
  // this.messages,
  // this.stop,
  // this.temperature,
  // this.topP,
  // this.maxTokens,
  // this.maxCompletionTokens,
  // this.presencePenalty,
  // this.frequencyPenalty,
  // this.logitBias,
  // this.user,
  // logprobs,
  // this.topLogprobs,
  // this.n,
  // this.parallelToolCalls,
  // this.seed,
  // this.streamOptions,
  // this.responseFormat,
  // this.tools,
  // this.toolChoice);
  // }

  /**
   * Define the model behavior towards calling functions.
   *
   * <p>
   * Example:
   *
   * <ul>
   * <li><code>.withToolChoice(ClaudeAiToolChoice.NONE)</code>
   * <li><code>.withToolChoice(ClaudeAiToolChoice.OPTIONAL)</code>
   * <li><code>.withToolChoice(ClaudeAiToolChoice.REQUIRED)</code>
   * <li><code>.withToolChoice(ClaudeAiToolChoice.function("fibonacci")</code>
   * </ul>
   *
   * @param choice the generic tool choice.
   * @return the current ClaudeAiChatCompletionRequest instance.
   */
  @Nonnull
  @Tolerate
  public ClaudeAiChatCompletionRequest withToolChoice(@Nonnull final ClaudeAiToolChoice choice) {
    return this.withToolChoice(choice.toolChoice);
  }

  /**
   * Converts the request to a generated model class CreateChatCompletionRequest.
   *
   * @return the CreateChatCompletionRequest
   */
  CreateChatCompletionRequest createCreateChatCompletionRequest() {
    final var request = new CreateChatCompletionRequest();
    this.messages.forEach(
        message -> request.addMessagesItem(ClaudeAiUtils.createChatCompletionRequestMessage(message)));

    // request.stop(this.stop != null ?
    // CreateChatCompletionRequestAllOfStop.create(this.stop) : null);

    request.temperature(this.temperature);
    request.topP(this.topP);

    request.stream(null);
    request.maxTokens(this.maxTokens);
    // request.maxCompletionTokens(this.maxCompletionTokens);
    // request.presencePenalty(this.presencePenalty);
    // request.frequencyPenalty(this.frequencyPenalty);
    // request.logitBias(this.logitBias);
    // request.user(this.user);
    // request.logprobs(this.logprobs);
    // request.topLogprobs(this.topLogprobs);
    // request.n(this.n);
    // request.parallelToolCalls(this.parallelToolCalls);
    // request.seed(this.seed);
    // request.streamOptions(this.streamOptions);
    // request.responseFormat(this.responseFormat);
    request.tools(this.tools);
    request.toolChoice(this.toolChoice);
    // request.functionCall(null);
    // request.functions(null);
    return request;
  }
}
