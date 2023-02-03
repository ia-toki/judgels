package judgels.uriel.api.contest.clarification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarificationConfig.class)
public interface ContestClarificationConfig {
    boolean getCanCreate();
    boolean getCanSupervise();
    boolean getCanManage();
    List<String> getProblemJids();

    class Builder extends ImmutableContestClarificationConfig.Builder {}
}
