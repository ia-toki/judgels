package judgels.uriel.api.contest.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.user.rating.UserRating;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestRating.class)
public interface ContestRating {
    String getContestJid();
    UserRating getRating();

    class Builder extends ImmutableContestRating.Builder {}
}
