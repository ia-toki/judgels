package judgels.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemBundleSubmissionsResponse.class)
public interface ProblemBundleSubmissionsResponse {
    Page<BundleSubmission> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemBundleSubmissionsResponse.Builder {}
}
