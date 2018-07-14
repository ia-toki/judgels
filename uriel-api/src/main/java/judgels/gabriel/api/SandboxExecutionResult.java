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
}
