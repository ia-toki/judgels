package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandboxExecutionResult.class)
public interface SandboxExecutionResult {
    SandboxExecutionStatus getStatus();
    int getTimeInMilliseconds();
    int getMemoryInKilobytes();
    String getMessage();

    static SandboxExecutionResult internalError(String message) {
        return new Builder()
                .status(SandboxExecutionStatus.INTERNAL_ERROR)
                .timeInMilliseconds(-1)
                .memoryInKilobytes(-1)
                .message(message)
                .build();
    }

    class Builder extends ImmutableSandboxExecutionResult.Builder {}
}
