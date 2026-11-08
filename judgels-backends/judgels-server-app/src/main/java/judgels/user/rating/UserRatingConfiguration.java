package judgels.user.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRatingConfiguration.class)
public interface UserRatingConfiguration {
    UserRatingConfiguration DEFAULT = new Builder().build();

    @Value.Default
    default boolean getEnabled() {
        return false;
    }

    class Builder extends ImmutableUserRatingConfiguration.Builder {}
}
