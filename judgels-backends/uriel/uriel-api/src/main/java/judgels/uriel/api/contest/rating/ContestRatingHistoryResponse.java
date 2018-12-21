package judgels.uriel.api.contest.rating;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestRatingHistoryResponse.class)
public interface ContestRatingHistoryResponse {
    List<ContestRating> getData();
    Map<String, ContestInfo> getContestMap();

    class Builder extends ImmutableContestRatingHistoryResponse.Builder {}
}
