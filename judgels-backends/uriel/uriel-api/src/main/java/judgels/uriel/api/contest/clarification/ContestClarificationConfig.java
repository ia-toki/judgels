package judgels.uriel.api.contest.clarification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarificationConfig.class)
public interface ContestClarificationConfig {
    boolean getIsAllowedToCreateClarification();

    class Builder extends ImmutableContestClarificationConfig.Builder {}
}
