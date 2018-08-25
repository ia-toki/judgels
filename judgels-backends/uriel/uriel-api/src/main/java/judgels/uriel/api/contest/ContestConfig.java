package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestConfig.class)
public interface ContestConfig {
    boolean getIsAllowedToCreateContest();

    class Builder extends ImmutableContestConfig.Builder {}
}
