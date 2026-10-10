package judgels.api.catalog.problem.version;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.catalog.CatalogVersion;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemVersionsResponse.class)
public interface ProblemVersionsResponse {
    List<CatalogVersion> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemVersionsResponse.Builder {}
}
