package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.user.User;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestant.class)
public interface ContestContestant {
    User getContestant();

    class Builder extends ImmutableContestContestant.Builder {}
}
