package judgels.uriel.api.contest.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.jophiel.api.user.rating.UserRating;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestHistoryEvent.class)
public interface ContestHistoryEvent {
    String getContestJid();
    int getRank();
    Optional<UserRating> getRating();

    class Builder extends ImmutableContestHistoryEvent.Builder {}
}
