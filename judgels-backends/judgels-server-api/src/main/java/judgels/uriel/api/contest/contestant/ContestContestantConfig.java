package judgels.uriel.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantConfig.class)
public interface ContestContestantConfig {
    boolean getCanManage();

    class Builder extends ImmutableContestContestantConfig.Builder {}
}
