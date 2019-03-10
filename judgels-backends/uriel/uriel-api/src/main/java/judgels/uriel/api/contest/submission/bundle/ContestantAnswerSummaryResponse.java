package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.BasicProfile;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestantAnswerSummaryResponse.class)
public interface ContestantAnswerSummaryResponse {
    BasicProfile getProfile();
    ContestSubmissionConfig getConfig();
    Map<String, List<ItemSubmission>> getSubmissionsByProblemJid();
    Map<String, String> getProblemAliasesByProblemJid();
    Map<String, String> getProblemNamesByProblemJid();
    Map<String, Integer> getItemNumberByItemJid();

    class Builder extends ImmutableContestantAnswerSummaryResponse.Builder {}
}
