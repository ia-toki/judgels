package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItemSubmission.class)
public interface ItemSubmission {
    long getId();
    String getJid();
    String getContainerJid();
    String getProblemJid();
    String getItemJid();
    String getAnswer();
    String getUserJid();
    Instant getTime();
    Optional<ItemSubmissionGrading> getGrading();

    static ItemSubmission withoutGrading(ItemSubmission submission) {
        return new ItemSubmission.Builder().from(submission).grading(Optional.empty()).build();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableItemSubmissionGrading.class)
    interface ItemSubmissionGrading {
        Verdict getVerdict();
        Optional<Integer> getScore();

        class Builder extends ImmutableItemSubmissionGrading.Builder {}
    }

    class Builder extends ImmutableItemSubmission.Builder {}
}
