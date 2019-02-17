package judgels.uriel.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import judgels.fs.local.LocalFsConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionConfiguration.class)
public interface SubmissionConfiguration {
    SubmissionConfiguration DEFAULT = new Builder()
            .fs(new LocalFsConfiguration.Builder()
                    .build())
            .build();

    FsConfiguration getFs();

    class Builder extends ImmutableSubmissionConfiguration.Builder {}
}
