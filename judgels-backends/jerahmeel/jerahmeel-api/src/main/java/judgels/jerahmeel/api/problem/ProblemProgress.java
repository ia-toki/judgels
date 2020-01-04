package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.Verdict;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemProgress.class)
public interface ProblemProgress {
    Verdict getVerdict();
    int getScore();

    class Builder extends ImmutableProblemProgress.Builder {}
}
