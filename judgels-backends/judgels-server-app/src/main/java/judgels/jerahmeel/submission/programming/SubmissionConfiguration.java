package judgels.jerahmeel.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionConfiguration.class)
public interface SubmissionConfiguration {
    FsConfiguration getFs();

    class Builder extends ImmutableSubmissionConfiguration.Builder {}
}
