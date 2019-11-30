package judgels.jerahmeel.api.problemset.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetSubmissionConfig.class)
public interface ProblemSetSubmissionConfig {
    boolean getCanManage();
    List<String> getProblemJids();

    class Builder extends ImmutableProblemSetSubmissionConfig.Builder {}
}
