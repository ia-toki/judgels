package judgels.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemsResponse.class)
public interface ProblemsResponse {
    Page<Problem> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemsResponse.Builder {}
}
