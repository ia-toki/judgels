package judgels.jophiel.api.user.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRatingEvent.class)
public interface RatingEvent {
    Instant getTime();
    String getEventJid();

    class Builder extends ImmutableRatingEvent.Builder {}
}
