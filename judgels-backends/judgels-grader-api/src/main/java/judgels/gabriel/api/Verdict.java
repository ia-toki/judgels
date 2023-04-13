package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonDeserialize(using = Verdict.VerdictDeserializer.class)
public enum Verdict {
    @JsonProperty ACCEPTED("AC", "Accepted"),
    @JsonProperty OK("OK", "OK"),
    @JsonProperty SKIPPED("SKP", "Skipped"),
    @JsonProperty WRONG_ANSWER("WA", "Wrong Answer"),
    @JsonProperty TIME_LIMIT_EXCEEDED("TLE", "Time Limit Exceeded"),
    @JsonProperty RUNTIME_ERROR("RTE", "Runtime Error"),
    @JsonProperty COMPILATION_ERROR("CE", "Compilation Error"),
    @JsonProperty INTERNAL_ERROR("!!!", "Internal Error"),
    @JsonProperty PENDING("?", "Pending");

    private String code;
    private String name;

    Verdict(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @JsonValue
    Map<String, String> toValue() {
        Map<String, String> val = new HashMap<>();
        val.put("code", code);
        return val;
    }

    static class VerdictDeserializer extends JsonDeserializer<Verdict> {
        @Override
        public Verdict deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode node = jp.readValueAsTree();
            String code = node.get("code").asText();
            return Verdicts.fromCode(code);
        }
    }
}
