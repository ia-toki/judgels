package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;


@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleSubmissionData.class)
public interface ContestBundleSubmissionData {
    String getContestJid();
    String getProblemJid();
    String getItemJid();
    String getValue();

    class Builder extends ImmutableContestBundleSubmissionData.Builder {}
}
