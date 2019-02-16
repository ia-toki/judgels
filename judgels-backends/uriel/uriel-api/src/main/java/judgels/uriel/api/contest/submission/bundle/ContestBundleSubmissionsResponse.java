package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.BundleSubmission;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleSubmissionsResponse.class)
public interface ContestBundleSubmissionsResponse {
    Page<BundleSubmission> getData();
    ContestSubmissionConfig getConfig();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableContestBundleSubmissionsResponse.Builder {}
}
