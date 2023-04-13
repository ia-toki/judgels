package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.persistence.api.Page;
import judgels.uriel.api.contest.role.ContestRole;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestsResponse.class)
public interface ContestsResponse {
    Page<Contest> getData();
    Map<String, ContestRole> getRolesMap();
    ContestConfig getConfig();

    class Builder extends ImmutableContestsResponse.Builder {}
}
