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
    @JsonProperty ACCEPTED("AC"),
    @JsonProperty OK("OK"),
    @JsonProperty SKIPPED("SKP"),
    @JsonProperty WRONG_ANSWER("WA"),
    @JsonProperty TIME_LIMIT_EXCEEDED("TLE"),
    @JsonProperty RUNTIME_ERROR("RTE"),
    @JsonProperty COMPILATION_ERROR("CE"),
    @JsonProperty INTERNAL_ERROR("!!!"),
    @JsonProperty PENDING("?");

    private String code;

    Verdict(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
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
