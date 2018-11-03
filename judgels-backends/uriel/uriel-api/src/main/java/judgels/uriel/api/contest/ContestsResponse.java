package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestsResponse.class)
public interface ContestsResponse {
    Page<Contest> getData();
    ContestConfig getConfig();

    class Builder extends ImmutableContestsResponse.Builder {}
}
