package judgels.jerahmeel.api.problemset.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetItemSubmissionData.class)
public interface ProblemSetItemSubmissionData {
    String getProblemSetJid();
    String getProblemJid();
    String getItemJid();
    String getAnswer();

    class Builder extends ImmutableProblemSetItemSubmissionData.Builder {}
}
