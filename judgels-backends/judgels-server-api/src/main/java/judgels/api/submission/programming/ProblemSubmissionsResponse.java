package judgels.api.submission.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSubmissionsResponse.class)
public interface ProblemSubmissionsResponse {
    Page<Submission> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemSubmissionsResponse.Builder {}
}
