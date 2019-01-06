package judgels.uriel.api.contest.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionConfig.class)
public interface ContestSubmissionConfig {
    boolean getCanSupervise();
    boolean getCanManage();
    List<String> getUserJids();
    List<String> getProblemJids();

    class Builder extends ImmutableContestSubmissionConfig.Builder {}
}
