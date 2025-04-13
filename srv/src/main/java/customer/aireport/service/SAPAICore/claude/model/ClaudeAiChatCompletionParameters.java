package customer.aireport.service.SAPAICore.claude.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.annotations.Beta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/** OpenAI chat completion input parameters. */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Beta
public class ClaudeAiChatCompletionParameters extends ClaudeAiCompletionParameters {
  /** A list of messages comprising the conversation so far. */
  @JsonProperty("messages")
  private List<ClaudeAiChatMessage> messages;

  @JsonProperty("system")
  private String systemMessage;

  /**
   * A list of tools the model may call. Currently, only functions are supported
   * as a tool. Use this
   * to provide a list of functions the model may generate JSON inputs for.
   */
  @JsonProperty("tools")
  @Setter(onParam_ = @Nullable)
  private List<ClaudeAiChatCompletionTool> tools;

  /**
   * Controls which (if any) function is called by the model.
   *
   * <p>
   * - {@code none} means the model will not call a function and instead generates
   * a message. -
   * {@code auto} means the model can pick between generating a message or calling
   * a function. -
   * Specifying a particular function via
   * {@code {"type": "function", "function": {"name":
   * "my_function"}}} forces the model to call that function.
   */
  @JsonProperty("tool_choice")
  @Setter(AccessLevel.NONE)
  @Nullable
  private ToolChoice toolChoice;



  /**
   * Controls which (if any) function is called by the model. `none` means the
   * model will not call a
   * function and instead generates a message.
   *
   * @return ${code this} instance for chaining.
   */
  @Nonnull
  public ClaudeAiChatCompletionParameters setToolChoiceAuto() {
    toolChoice = ToolChoiceType.AUTO;
    return this;
  }

  /**
   * Controls which (if any) function is called by the model. Specifying a
   * particular function
   * forces the model to call that function.
   *
   * @param functionName The name of the function to call.
   * @return ${code this} instance for chaining.
   */
  @Nonnull
  public ClaudeAiChatCompletionParameters setToolChoiceFunction(@Nonnull final String toolName) {
    toolChoice = new ToolToolChoice().setName(toolName);
    return this;
  }

  /** Tool choice options. */
  @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", defaultImpl = ToolChoiceType.class) // This is the
                                                                                                        // field that
                                                                                                        // determines
                                                                                                        // the class
                                                                                                        // type
  @JsonSubTypes({ @JsonSubTypes.Type(value = ToolToolChoice.class, name = "tool") })
  interface ToolChoice {
  }

  @RequiredArgsConstructor
  private enum ToolChoiceType implements ToolChoice {
    /**
    ** any type
     */
    ANY("any"),

    /**
     * `auto` means the model can pick between generating a message or calling a
     * function.
     */
    AUTO("auto");

    @JsonValue
    @Nonnull
    private final String type;
  }

  @EqualsAndHashCode
  @ToString
  private static class ToolToolChoice implements ToolChoice {

    @JsonProperty("type")
    @Getter(onMethod_ = @Nullable)
    private final String type = "tool";

    @JsonProperty("name")
    @Getter(onMethod_ = @Nonnull)
    @Setter(onParam_ = @Nonnull)
    private String name;


  }

 
  /**
   * Add messages to the conversation.
   *
   * @param messages The messages to add.
   * @return this instance for chaining.
   */
  @Nonnull
  public ClaudeAiChatCompletionParameters addMessages(@Nonnull final ClaudeAiChatMessage... messages) {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    this.messages.addAll(Arrays.asList(messages));
    return this;
  }

  @Nullable
  public ClaudeAiChatCompletionParameters setSystemPrompt(@Nullable final String systemPrompt) {
    this.systemMessage = systemPrompt;
    return this;
  }
}
