package judgels.jophiel.user.rating;

import org.immutables.value.Value;

@Value.Immutable
public abstract class UserWithRating {
    public abstract String getUserJid();
    public abstract int getRating();

    public static UserWithRating of(String userJid, int rating) {
        return ImmutableUserWithRating.builder().userJid(userJid).rating(rating).build();
    }
}
