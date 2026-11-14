package judgels.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRatingConfiguration.class)
public interface RatingConfiguration {
    RatingConfiguration DEFAULT = new Builder().build();

    @Value.Default
    default boolean getEnabled() {
        return false;
    }

    class Builder extends ImmutableRatingConfiguration.Builder {}
}
