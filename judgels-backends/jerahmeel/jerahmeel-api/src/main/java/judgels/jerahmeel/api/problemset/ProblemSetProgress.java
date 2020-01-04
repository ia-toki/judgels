package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProgress.class)
public interface ProblemSetProgress {
    int getScore();
    int getTotalProblems();

    class Builder extends ImmutableProblemSetProgress.Builder {}
}
