package judgels.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSubmissionWithSourceResponse.class)
public interface ProblemSubmissionWithSourceResponse {
    SubmissionWithSource getData();
    Profile getProfile();

    class Builder extends ImmutableProblemSubmissionWithSourceResponse.Builder {}
}
