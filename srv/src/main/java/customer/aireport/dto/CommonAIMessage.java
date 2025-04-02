package customer.aireport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommonAIMessage(@JsonProperty("role") String role,
        @JsonProperty("message") String message) {
}
