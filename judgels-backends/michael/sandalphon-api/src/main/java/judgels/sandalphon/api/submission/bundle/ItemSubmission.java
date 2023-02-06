package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItemSubmission.class)
public interface ItemSubmission {
    String getJid();
    String getContainerJid();
    String getProblemJid();
    String getItemJid();
    String getAnswer();
    String getUserJid();
    Instant getTime();
    Optional<Grading> getGrading();

    static ItemSubmission withoutGrading(ItemSubmission submission) {
        return new ItemSubmission.Builder().from(submission).grading(Optional.empty()).build();
    }

    class Builder extends ImmutableItemSubmission.Builder {}
}
