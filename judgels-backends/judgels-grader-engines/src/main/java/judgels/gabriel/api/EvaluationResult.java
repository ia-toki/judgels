package judgels.gabriel.api;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface EvaluationResult {
    TestCaseVerdict getVerdict();
    Optional<SandboxExecutionResult> getExecutionResult();
    Optional<String> getRevealedInput();
    Optional<String> getRevealedSolutionOutput();

    static EvaluationResult skippedResult() {
        TestCaseVerdict verdict = new TestCaseVerdict.Builder().verdict(Verdict.SKIPPED).build();
        return new Builder().verdict(verdict).build();
    }

    class Builder extends ImmutableEvaluationResult.Builder {}
}
