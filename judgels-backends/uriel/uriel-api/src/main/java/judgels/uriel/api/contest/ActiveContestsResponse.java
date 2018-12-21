package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.uriel.api.contest.role.ContestRole;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableActiveContestsResponse.class)
public interface ActiveContestsResponse {
    List<Contest> getData();
    Map<String, ContestRole> getRolesMap();

    class Builder extends ImmutableActiveContestsResponse.Builder {}
}
