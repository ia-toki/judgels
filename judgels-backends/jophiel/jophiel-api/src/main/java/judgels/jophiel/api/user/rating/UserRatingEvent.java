package judgels.jophiel.api.user.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRatingEvent.class)
public interface UserRatingEvent {
    Instant getTime();
    String getEventJid();
    UserRating getRating();

    class Builder extends ImmutableUserRatingEvent.Builder {}
}
