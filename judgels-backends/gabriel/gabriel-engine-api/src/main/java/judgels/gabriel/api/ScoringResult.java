package judgels.gabriel.api;

import org.immutables.value.Value;

@Value.Immutable
public interface ScoringResult {
    Verdict getVerdict();
    String getScore();

    class Builder extends ImmutableScoringResult.Builder {}
}
