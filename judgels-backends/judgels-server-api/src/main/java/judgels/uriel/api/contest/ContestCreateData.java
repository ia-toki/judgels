package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestCreateData.class)
public interface ContestCreateData {
    String getSlug();

    class Builder extends ImmutableContestCreateData.Builder {}
}
