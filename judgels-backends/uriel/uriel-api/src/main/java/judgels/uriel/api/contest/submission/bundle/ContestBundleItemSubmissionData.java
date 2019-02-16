package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;


@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleItemSubmissionData.class)
public interface ContestBundleItemSubmissionData {
    String getContestJid();
    String getProblemJid();
    String getItemJid();
    String getAnswer();

    class Builder extends ImmutableContestBundleItemSubmissionData.Builder {}
}
