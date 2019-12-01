package judgels.sandalphon.api.submission.programming;

import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;

public interface SubmissionsResponse {
    Page<Submission> getData();
    Map<String, Profile> getProfilesMap();
    Map<String, String> getProblemAliasesMap();
}
