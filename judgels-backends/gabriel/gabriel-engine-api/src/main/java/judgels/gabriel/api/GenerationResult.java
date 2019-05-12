package judgels.gabriel.api;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface GenerationResult {
    Optional<TestCaseVerdict> getVerdict();
    SandboxExecutionResult getExecutionResult();

    class Builder extends ImmutableGenerationResult.Builder {}
}
