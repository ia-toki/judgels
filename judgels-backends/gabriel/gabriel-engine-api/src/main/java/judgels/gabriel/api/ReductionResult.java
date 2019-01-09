package judgels.gabriel.api;

import org.immutables.value.Value;

@Value.Immutable
public interface ReductionResult {
    NormalVerdict getVerdict();
    int getScore();

    class Builder extends ImmutableReductionResult.Builder {}
}
