package judgels.uriel.api.contest.clarification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarificationsResponse.class)
public interface ContestClarificationsResponse {
    Page<ContestClarification> getData();
    ContestClarificationConfig getConfig();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, String> getProblemNamesMap();

    class Builder extends ImmutableContestClarificationsResponse.Builder {}
}
