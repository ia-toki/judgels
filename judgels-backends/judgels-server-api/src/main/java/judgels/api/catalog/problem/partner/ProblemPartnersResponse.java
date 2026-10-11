package judgels.api.catalog.problem.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.catalog.Partner;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemPartnersResponse.class)
public interface ProblemPartnersResponse {
    List<Partner> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemPartnersResponse.Builder {}
}
