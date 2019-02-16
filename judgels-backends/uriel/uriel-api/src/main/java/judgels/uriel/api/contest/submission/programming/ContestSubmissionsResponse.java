package judgels.uriel.api.contest.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionsResponse.class)
public interface ContestSubmissionsResponse {
    Page<Submission> getData();
    ContestSubmissionConfig getConfig();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableContestSubmissionsResponse.Builder {}
}
