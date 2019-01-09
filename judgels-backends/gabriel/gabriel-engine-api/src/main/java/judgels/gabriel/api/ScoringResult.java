package judgels.gabriel.api;

import org.immutables.value.Value;

@Value.Immutable
public interface ScoringResult {
    ScoringVerdict getVerdict();
    String getScore();

    class Builder extends ImmutableScoringResult.Builder {}
}
