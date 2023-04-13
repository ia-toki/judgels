package judgels.jerahmeel.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jerahmeel.api.submission.SubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionsResponse.class)
public interface SubmissionsResponse extends judgels.sandalphon.api.submission.programming.SubmissionsResponse {
    Map<String, String> getProblemNamesMap();
    Map<String, String> getContainerNamesMap();
    Map<String, List<String>> getContainerPathsMap();
    SubmissionConfig getConfig();

    class Builder extends ImmutableSubmissionsResponse.Builder {}
}
