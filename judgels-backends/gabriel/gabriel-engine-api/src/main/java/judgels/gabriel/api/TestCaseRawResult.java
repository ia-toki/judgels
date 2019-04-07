package judgels.gabriel.api;

import org.immutables.value.Value;

@Value.Immutable
public interface TestCaseRawResult {
    Verdict getVerdict();
    String getScore();

    class Builder extends ImmutableTestCaseRawResult.Builder {}
}
