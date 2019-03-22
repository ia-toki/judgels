package judgels.uriel.api.contest.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import judgels.uriel.api.contest.submission.ContestSubmissionConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestantAnswerSummaryResponse.class)
public interface ContestantAnswerSummaryResponse {
    Profile getProfile();
    ContestSubmissionConfig getConfig();
    Map<String, List<String>> getItemJidsByProblemJid();
    Map<String, ItemSubmission> getSubmissionsByItemJid();
    Map<String, ItemType> getItemTypesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, String> getProblemNamesMap();

    class Builder extends ImmutableContestantAnswerSummaryResponse.Builder {}
}
