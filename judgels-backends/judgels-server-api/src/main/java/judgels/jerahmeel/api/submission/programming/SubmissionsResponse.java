package judgels.jerahmeel.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.CursorPage;
import judgels.sandalphon.api.submission.programming.Submission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionsResponse.class)
public interface SubmissionsResponse {
    CursorPage<Submission> getData();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, String> getProblemNamesMap();
    Map<String, String> getContainerNamesMap();
    Map<String, List<String>> getContainerPathsMap();
    SubmissionConfig getConfig();

    class Builder extends ImmutableSubmissionsResponse.Builder {}
}
