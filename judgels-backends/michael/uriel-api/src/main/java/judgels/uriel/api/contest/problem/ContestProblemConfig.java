package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblemConfig.class)
public interface ContestProblemConfig {
    boolean getCanManage();

    class Builder extends ImmutableContestProblemConfig.Builder {}
}
