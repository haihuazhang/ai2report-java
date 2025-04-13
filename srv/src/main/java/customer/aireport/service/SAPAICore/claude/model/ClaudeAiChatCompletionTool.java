package customer.aireport.service.SAPAICore.claude.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.annotations.Beta;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/** OpenAI tool signature. */
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@Beta
public class ClaudeAiChatCompletionTool {
  /**
   * Specifies a tool the model should use. Use to force the model to call a
   * specific function.
   */
  /**
   * Name of the function to be called. Must be a-z, A-Z, 0-9, or contain
   * underscores and dashes,
   * with a maximum length of 64.
   */
  @JsonProperty("name")
  @Setter(onParam_ = @Nullable)
  private String name;

  /** Description of the function. */
  @JsonProperty("description")
  @Setter(onParam_ = @Nullable)
  private String description;

  /**
   * JSON Schema for the function input parameters.
   *
   * <p>
   * <b>Note</b>: As mentioned by <a
   * href=
   * "https://community.openai.com/t/whitch-json-schema-version-should-function-calling-use/283535/4">OpenAI</a>,
   * it follows JSON Schema 7 (2020-12). Not all JSON Schema parameters in the
   * specification are
   * supported by OpenAI.
   */
  // @JsonProperty("parameters")
  @JsonProperty("input_schema")
  @Setter(onParam_ = @Nullable)
  private Map<String, Object> input_schema;
}
