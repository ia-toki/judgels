package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestGroup.class)
public interface ContestGroup {
    String getJid();
    String getSlug();
    String getName();

    class Builder extends ImmutableContestGroup.Builder {}
}
