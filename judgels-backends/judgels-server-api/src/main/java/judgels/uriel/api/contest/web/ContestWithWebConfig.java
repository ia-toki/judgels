package judgels.uriel.api.contest.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.uriel.api.contest.Contest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestWithWebConfig.class)
public interface ContestWithWebConfig {
    Contest getContest();
    ContestWebConfig getConfig();

    class Builder extends ImmutableContestWithWebConfig.Builder {}
}
