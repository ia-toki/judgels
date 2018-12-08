package judgels.uriel.api.contest.file;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestFileConfig.class)
public interface ContestFileConfig {
    boolean getCanManage();

    class Builder extends ImmutableContestFileConfig.Builder {}
}
