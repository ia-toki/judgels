package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.user.PublicUser;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestant.class)
public interface ContestContestant {
    PublicUser getContestant();

    class Builder extends ImmutableContestContestant.Builder {}
}
