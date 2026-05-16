package judgels.api.submission.bundle;

import java.util.List;
import java.util.Map;
import judgels.api.problem.bundle.ItemType;
import judgels.api.profile.Profile;

public interface SubmissionSummaryResponse {
    Profile getProfile();
    Map<String, List<String>> getItemJidsByProblemJid();
    Map<String, ItemSubmission> getSubmissionsByItemJid();
    Map<String, ItemType> getItemTypesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, String> getProblemNamesMap();
}
