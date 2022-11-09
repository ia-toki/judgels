package judgels.sandalphon.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionWithSourceResponse.class)
public interface SubmissionWithSourceResponse {
    SubmissionWithSource getData();
    Profile getProfile();
    String getProblemName();
    String getProblemAlias();
    List<String> getContainerPath();
    String getContainerName();

    class Builder extends ImmutableSubmissionWithSourceResponse.Builder {}
}
