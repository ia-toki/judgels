package judgels.sandalphon.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import judgels.sandalphon.api.submission.Grading;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProgrammingSubmission.class)
public interface ProgrammingSubmission {
    long getId();
    String getJid();
    String getUserJid();
    String getProblemJid();
    String getContainerJid();
    String getGradingEngine();
    String getGradingLanguage();
    Instant getTime();
    Optional<Grading> getLatestGrading();

    class Builder extends ImmutableProgrammingSubmission.Builder {}
}
