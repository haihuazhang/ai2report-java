package customer.aireport.processor;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface AIResponseProcessor<T> {
    T process(JsonNode arrayItem);
}