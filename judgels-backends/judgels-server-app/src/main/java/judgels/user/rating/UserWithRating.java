package judgels.user.rating;

import judgels.api.user.rating.UserRating;
import org.immutables.value.Value;

@Value.Immutable
public abstract class UserWithRating {
    public abstract String getUserJid();
    public abstract UserRating getRating();

    public static UserWithRating of(String userJid, UserRating rating) {
        return ImmutableUserWithRating.builder().userJid(userJid).rating(rating).build();
    }
}
