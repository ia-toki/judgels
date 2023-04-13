package judgels.sandalphon.api.submission.bundle;

import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.bundle.ItemType;

public interface ItemSubmissionsResponse {
    Page<ItemSubmission> getData();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, Integer> getItemNumbersMap();
    Map<String, ItemType> getItemTypesMap();
}
