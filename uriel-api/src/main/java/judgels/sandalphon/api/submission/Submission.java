package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
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
    Grading getLatestGrading();

    class Builder extends ImmutableSubmission.Builder {}
}
