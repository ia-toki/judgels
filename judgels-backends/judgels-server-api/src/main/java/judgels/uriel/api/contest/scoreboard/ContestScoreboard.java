package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.NoClass;
import java.time.Instant;
import judgels.uriel.api.contest.ContestStyle;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestScoreboard.class)
public interface ContestScoreboard {
    ContestScoreboardType getType();
    ContestStyle getStyle();
    int getTotalEntries();
    Instant getUpdatedTime();

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "style",
            visible = true,
            defaultImpl = NoClass.class
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImmutableBundleScoreboard.class),
            @JsonSubTypes.Type(value = ImmutableGcjScoreboard.class),
            @JsonSubTypes.Type(value = ImmutableIcpcScoreboard.class),
            @JsonSubTypes.Type(value = ImmutableIoiScoreboard.class),
            @JsonSubTypes.Type(value = ImmutableTrocScoreboard.class)
    })
    Scoreboard getScoreboard();

    class Builder extends ImmutableContestScoreboard.Builder {}
}
