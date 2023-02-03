package judgels.uriel.api.contest.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.uriel.api.contest.ContestInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestHistoryResponse.class)
public interface ContestHistoryResponse {
    List<ContestHistoryEvent> getData();
    Map<String, ContestInfo> getContestsMap();

    class Builder extends ImmutableContestHistoryResponse.Builder {}
}
