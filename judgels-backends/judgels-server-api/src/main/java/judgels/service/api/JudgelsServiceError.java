package judgels.service.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JudgelsServiceError {
    private final int code;
    private final String message;
    @Nullable private final Map<String, Object> args;

    @JsonCreator
    public JudgelsServiceError(
            @JsonProperty("code") int code,
            @JsonProperty("message") String message,
            @JsonProperty("args") @Nullable Map<String, Object> args) {

        this.code = code;
        this.message = message;
        this.args = args;
    }

    @JsonProperty("code")
    public Integer getCode() {
        return this.code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return this.message;
    }

    @JsonProperty("args")
    @Nullable
    public Map<String, Object> getArgs() {
        return this.args;
    }
}
