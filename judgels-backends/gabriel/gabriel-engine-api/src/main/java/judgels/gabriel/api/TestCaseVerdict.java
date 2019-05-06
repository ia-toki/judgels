package judgels.gabriel.api;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface TestCaseVerdict {
    Verdict getVerdict();
    Optional<Double> getPoints();
    Optional<String> getFeedback();

    default String getScore() {
        String score = "";
        if (getPoints().isPresent()) {
            score += getPoints().get();
        }
        if (getFeedback().isPresent()) {
            if (!score.isEmpty()) {
                score += " [" + getFeedback().get() + "]";
            } else {
                score += getFeedback().get();
            }
        }
        return score;
    }

    class Builder extends ImmutableTestCaseVerdict.Builder {}
}
