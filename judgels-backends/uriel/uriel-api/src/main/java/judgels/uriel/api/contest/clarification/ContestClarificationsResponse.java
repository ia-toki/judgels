package judgels.uriel.api.contest.clarification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarificationsResponse.class)
public interface ContestClarificationsResponse {
    List<ContestClarification> getData();
    Map<String, String> getProblemAliasesMap();
    Map<String, String> getProblemNamesMap();

    class Builder extends ImmutableContestClarificationsResponse.Builder {}
}
