package judgels.gabriel.api;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface TestCaseVerdict {
    Verdict getVerdict();
    Optional<Double> getPoints();
    Optional<String> getFeedback();

    class Builder extends ImmutableTestCaseVerdict.Builder {}
}
