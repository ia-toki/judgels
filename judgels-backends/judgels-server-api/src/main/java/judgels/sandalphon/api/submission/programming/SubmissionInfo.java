package judgels.sandalphon.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionInfo.class)
public interface SubmissionInfo {
    long getId();
    Profile getProfile();

    class Builder extends ImmutableSubmissionInfo.Builder {}
}
