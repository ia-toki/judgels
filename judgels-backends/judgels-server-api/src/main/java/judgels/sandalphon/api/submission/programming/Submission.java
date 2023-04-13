package judgels.sandalphon.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmission.class)
public interface Submission {
    long getId();
    String getJid();
    String getUserJid();
    String getProblemJid();
    String getContainerJid();
    String getGradingEngine();
    String getGradingLanguage();
    Instant getTime();
    Optional<Grading> getLatestGrading();

    class Builder extends ImmutableSubmission.Builder {}
}
