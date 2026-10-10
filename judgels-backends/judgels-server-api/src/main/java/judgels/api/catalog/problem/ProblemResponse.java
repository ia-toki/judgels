package judgels.api.catalog.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemResponse.class)
public interface ProblemResponse {
    Problem getData();
    Map<ProblemSetterRole, List<String>> getSetterJidsMap();
    Set<String> getTopicTags();
    boolean getHasLocalChanges();
    boolean getHasEditorial();
    ProblemConfig getConfig();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemResponse.Builder {}
}
