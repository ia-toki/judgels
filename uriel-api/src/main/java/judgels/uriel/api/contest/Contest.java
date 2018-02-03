package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContest.class)
public interface Contest {
    String getJid();
    String getName();

    class Builder extends ImmutableContest.Builder {}
}
