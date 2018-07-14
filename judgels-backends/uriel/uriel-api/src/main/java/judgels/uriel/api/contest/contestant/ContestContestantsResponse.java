package judgels.uriel.api.contest.contestant;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import java.util.Set;
import judgels.jophiel.api.user.UserInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantsResponse.class)
public interface ContestContestantsResponse {
    Set<String> getData();
    Map<String, UserInfo> getUsersMap();
    Map<String, String> getUserCountriesMap();

    class Builder extends ImmutableContestContestantsResponse.Builder {}
}
