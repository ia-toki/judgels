package judgels.uriel.api.contest.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManagerConfig.class)
public interface ContestManagerConfig {
    boolean getCanManage();

    class Builder extends ImmutableContestManagerConfig.Builder {}
}
