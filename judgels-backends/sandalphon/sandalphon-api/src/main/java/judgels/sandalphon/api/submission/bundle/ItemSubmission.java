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
    Optional<Grading> getGrading();

    static ItemSubmission withoutGrading(ItemSubmission submission) {
        return new ItemSubmission.Builder().from(submission).grading(Optional.empty()).build();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableGrading.class)
    interface Grading {
        Verdict getVerdict();
        Optional<Integer> getScore();

        class Builder extends ImmutableGrading.Builder {}
    }

    class Builder extends ImmutableItemSubmission.Builder {}
}
