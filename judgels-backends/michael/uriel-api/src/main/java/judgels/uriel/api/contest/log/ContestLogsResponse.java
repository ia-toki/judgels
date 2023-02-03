package judgels.uriel.api.contest.log;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestLogsResponse.class)
public interface ContestLogsResponse {
    Page<ContestLog> getData();
    ContestLogConfig getConfig();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableContestLogsResponse.Builder {}
}
