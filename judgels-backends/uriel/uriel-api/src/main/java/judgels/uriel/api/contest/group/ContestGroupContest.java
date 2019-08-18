package judgels.uriel.api.contest.group;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestGroupContest.class)
public interface ContestGroupContest {
    String getContestJid();
    String getAlias();

    class Builder extends ImmutableContestGroupContest.Builder {}
}
