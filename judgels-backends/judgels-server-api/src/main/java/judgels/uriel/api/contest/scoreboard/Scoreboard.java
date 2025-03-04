package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "style")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableBundleScoreboard.class),
        @JsonSubTypes.Type(value = ImmutableGcjScoreboard.class),
        @JsonSubTypes.Type(value = ImmutableIcpcScoreboard.class),
        @JsonSubTypes.Type(value = ImmutableIoiScoreboard.class),
        @JsonSubTypes.Type(value = ImmutableTrocScoreboard.class)
})
public interface Scoreboard {
    ScoreboardState getState();
    ScoreboardContent getContent();
}
