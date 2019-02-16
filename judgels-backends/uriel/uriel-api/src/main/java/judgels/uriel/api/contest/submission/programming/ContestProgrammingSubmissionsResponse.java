package judgels.uriel.api.contest.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.ProgrammingSubmission;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProgrammingSubmissionsResponse.class)
public interface ContestProgrammingSubmissionsResponse {
    Page<ProgrammingSubmission> getData();
    ContestSubmissionConfig getConfig();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableContestProgrammingSubmissionsResponse.Builder {}
}
