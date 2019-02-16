package judgels.sandalphon.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProgrammingSubmissionWithSourceResponse.class)
public interface ProgrammingSubmissionWithSourceResponse {
    ProgrammingSubmissionWithSource getData();
    Profile getProfile();
    String getProblemName();
    String getProblemAlias();
    String getContainerName();

    class Builder extends ImmutableProgrammingSubmissionWithSourceResponse.Builder {}
}
