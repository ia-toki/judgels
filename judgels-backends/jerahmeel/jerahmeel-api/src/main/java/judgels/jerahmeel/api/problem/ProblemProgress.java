package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemProgress.class)
public interface ProblemProgress {
    String getVerdict();
    int getScore();

    class Builder extends ImmutableProblemProgress.Builder {}
}
