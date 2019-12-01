package judgels.sandalphon.api.submission.bundle;

import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.problem.bundle.ItemType;

public interface SubmissionSummaryResponse {
    Profile getProfile();
    Map<String, List<String>> getItemJidsByProblemJid();
    Map<String, ItemSubmission> getSubmissionsByItemJid();
    Map<String, ItemType> getItemTypesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, String> getProblemNamesMap();
}
