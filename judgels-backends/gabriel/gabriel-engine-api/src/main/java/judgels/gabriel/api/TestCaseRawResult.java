package judgels.gabriel.api;

import org.immutables.value.Value;

@Value.Immutable
public interface TestCaseRawResult {
    NormalVerdict getVerdict();
    String getScore();

    class Builder extends ImmutableTestCaseRawResult.Builder {}
}
