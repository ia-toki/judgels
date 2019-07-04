package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSandboxExecutionResult.class)
public interface SandboxExecutionResult {
    SandboxExecutionStatus getStatus();
    int getTime();
    int getMemory();

    @Value.Default
    default int getWallTime() {
        return 0;
    }

    @Value.Default
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    default boolean getIsKilled() {
        return false;
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    Optional<Integer> getExitSignal();

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    Optional<String> getMessage();

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
