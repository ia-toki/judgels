package judgels.jophiel.api.user.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserRatingUpdateData.class)
public interface UserRatingUpdateData {
    Instant getTime();
    String getEventJid();
    Map<String, UserRating> getRatingsMap();

    class Builder extends ImmutableUserRatingUpdateData.Builder {}
}
