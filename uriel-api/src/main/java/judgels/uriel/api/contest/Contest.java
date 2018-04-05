package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContest.class)
public interface Contest {
    long getId();
    String getJid();
    String getName();
    String getDescription();
    ContestStyle getStyle();

    class Builder extends ImmutableContest.Builder {}
}
