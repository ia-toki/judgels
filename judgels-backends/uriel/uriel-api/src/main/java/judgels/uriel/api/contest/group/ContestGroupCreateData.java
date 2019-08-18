package judgels.uriel.api.contest.group;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestGroupCreateData.class)
public interface ContestGroupCreateData {
    String getSlug();

    class Builder extends ImmutableContestGroupCreateData.Builder {}
}
