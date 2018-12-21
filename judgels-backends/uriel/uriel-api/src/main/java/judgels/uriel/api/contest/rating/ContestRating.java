package judgels.uriel.api.contest.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestRating.class)
public interface ContestRating {
    String getContestJid();
    int getRating();

    class Builder extends ImmutableContestRating.Builder {}
}
