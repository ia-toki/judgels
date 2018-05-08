package judgels.uriel.api.contest.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestWebConfig.class)
public interface ContestWebConfig {
    Set<ContestTab> getVisibleTabs();

    class Builder extends ImmutableContestWebConfig.Builder {}
}
