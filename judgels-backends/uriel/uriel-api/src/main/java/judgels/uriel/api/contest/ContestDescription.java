package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

// Because DW can't directly render string as JSON formatted string
@Value.Immutable
@JsonDeserialize(as = ImmutableContestDescription.class)
public interface ContestDescription {
    String getDescription();

    class Builder extends ImmutableContestDescription.Builder {}
}
