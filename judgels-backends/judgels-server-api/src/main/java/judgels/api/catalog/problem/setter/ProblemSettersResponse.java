package judgels.api.catalog.problem.setter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.catalog.problem.ProblemSetterRole;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSettersResponse.class)
public interface ProblemSettersResponse {
    Map<ProblemSetterRole, List<String>> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemSettersResponse.Builder {}
}
