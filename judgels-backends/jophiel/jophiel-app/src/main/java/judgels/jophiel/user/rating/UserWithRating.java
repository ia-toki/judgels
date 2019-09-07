package judgels.jophiel.user.rating;

import judgels.jophiel.api.user.rating.UserRating;
import org.immutables.value.Value;

@Value.Immutable
public abstract class UserWithRating {
    public abstract String getUserJid();
    public abstract UserRating getRating();

    public static UserWithRating of(String userJid, UserRating rating) {
        return ImmutableUserWithRating.builder().userJid(userJid).rating(rating).build();
    }
}
