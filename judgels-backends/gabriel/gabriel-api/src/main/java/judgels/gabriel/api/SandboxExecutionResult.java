package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandboxExecutionResult.class)
public interface SandboxExecutionResult {
    SandboxExecutionStatus getStatus();
    int getTime();
    int getMemory();
    String getMessage();

    static SandboxExecutionResult plainMessage(String message) {
        return new Builder()
                .status(SandboxExecutionStatus.ZERO_EXIT_CODE)
                .time(0)
                .memory(0)
                .message(message)
                .build();
    }

    static SandboxExecutionResult internalError(String message) {
        return new Builder()
                .status(SandboxExecutionStatus.INTERNAL_ERROR)
                .time(-1)
                .memory(-1)
                .message(message)
                .build();
    }

    class Builder extends ImmutableSandboxExecutionResult.Builder {}
}
