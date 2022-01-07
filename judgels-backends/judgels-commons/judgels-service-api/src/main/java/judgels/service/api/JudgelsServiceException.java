package judgels.service.api;

import java.util.Map;
import javax.ws.rs.core.Response;

public class JudgelsServiceException extends RuntimeException {
    private final int code;
    private final String message;
    private final Map<String, Object> args;

    public JudgelsServiceException(Response.Status status, String message, Map<String, Object> args) {
        super((Throwable) null);

        this.code = status.getStatusCode();
        this.message = message;
        this.args = args;
    }

    public JudgelsServiceException(Response.Status status, String message) {
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
