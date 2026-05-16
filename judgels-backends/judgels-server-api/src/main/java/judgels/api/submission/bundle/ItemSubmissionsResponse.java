package judgels.api.submission.bundle;

import java.util.Map;
import judgels.api.problem.bundle.ItemType;
import judgels.api.profile.Profile;
import judgels.persistence.api.Page;

public interface ItemSubmissionsResponse {
    Page<ItemSubmission> getData();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, Integer> getItemNumbersMap();
    Map<String, ItemType> getItemTypesMap();
}
