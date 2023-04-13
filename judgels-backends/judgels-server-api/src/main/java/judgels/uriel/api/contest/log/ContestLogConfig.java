package judgels.uriel.api.contest.log;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestLogConfig.class)
public interface ContestLogConfig {
    List<String> getUserJids();
    List<String> getProblemJids();

    class Builder extends ImmutableContestLogConfig.Builder {}
}
