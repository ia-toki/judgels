package judgels.uriel.api.contest.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.rating.UserRating;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestRating.class)
public interface ContestRatingChanges {
    Map<String, UserRating> getRatingsMap();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestRatingChanges.Builder {}
}
