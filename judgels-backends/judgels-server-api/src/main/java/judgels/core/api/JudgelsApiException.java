package judgels.core.api;

import jakarta.ws.rs.core.Response;
import java.util.Map;

public class JudgelsApiException extends RuntimeException {
    private final int code;
    private final String message;
    private final Map<String, Object> args;

    public JudgelsApiException(Response.Status status, String message, Map<String, Object> args) {
        super((Throwable) null);

        this.code = status.getStatusCode();
        this.message = message;
        this.args = args;
    }

    public JudgelsApiException(Response.Status status, String message) {
        this(status, message, null);
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Map<String, Object> getArgs() {
        return args;
    }
}
