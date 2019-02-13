package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.submission.BundleSubmission;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestLatestBundleSubmissionsResponse.class)
public interface ContestLatestBundleSubmissionsResponse {
    Map<String, BundleSubmission> getSubmissionsByItemJid();
    ContestSubmissionConfig getConfig();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableContestLatestBundleSubmissionsResponse.Builder {}
}
