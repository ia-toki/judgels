package judgels.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemBundleSubmissionWithAnswerResponse.class)
public interface ProblemBundleSubmissionWithAnswerResponse {
    BundleSubmission getData();
    BundleAnswer getAnswer();
    Profile getProfile();

    class Builder extends ImmutableProblemBundleSubmissionWithAnswerResponse.Builder {}
}
