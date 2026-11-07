package judgels.api.catalog.problem.writer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemWritersResponse.class)
public interface ProblemWritersResponse {
    List<String> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemWritersResponse.Builder {}
}
