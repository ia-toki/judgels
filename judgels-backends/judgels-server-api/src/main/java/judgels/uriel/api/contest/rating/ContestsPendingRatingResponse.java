package judgels.uriel.api.contest.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.uriel.api.contest.Contest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestsPendingRatingResponse.class)
public interface ContestsPendingRatingResponse {
    List<Contest> getData();
    Map<String, ContestRatingChanges> getRatingChangesMap();

    class Builder extends ImmutableContestsPendingRatingResponse.Builder {}
}
