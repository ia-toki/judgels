package judgels.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.contest.role.ContestRole;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestsResponse.class)
public interface ContestsResponse {
    Page<Contest> getData();
    Map<String, ContestRole> getRolesMap();
    ContestConfig getConfig();

    class Builder extends ImmutableContestsResponse.Builder {}
}
