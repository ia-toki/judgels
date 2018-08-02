package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.info.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionWithSourceResponse.class)
public interface SubmissionWithSourceResponse {
    SubmissionWithSource getData();
    Profile getProfile();
    String getProblemName();
    String getProblemAlias();
    String getContainerName();

    class Builder extends ImmutableSubmissionWithSourceResponse.Builder {}
}
