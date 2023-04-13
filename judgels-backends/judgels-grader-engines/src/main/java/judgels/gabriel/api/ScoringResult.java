package judgels.gabriel.api;

import org.immutables.value.Value;

@Value.Immutable
public interface ScoringResult {
    TestCaseVerdict getVerdict();

    class Builder extends ImmutableScoringResult.Builder {}
}
