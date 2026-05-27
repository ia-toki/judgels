package tlx.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.profile.Profile;
import judgels.api.submission.SubmissionConfig;
import judgels.api.submission.programming.Submission;
import judgels.persistence.api.CursorPage;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingSubmissionsResponse.class)
public interface TrainingSubmissionsResponse {
    CursorPage<Submission> getData();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, String> getProblemNamesMap();
    Map<String, String> getContainerNamesMap();
    Map<String, List<String>> getContainerPathsMap();
    SubmissionConfig getConfig();

    class Builder extends ImmutableTrainingSubmissionsResponse.Builder {}
}
