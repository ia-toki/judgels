package judgels.uriel.api.contest.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionConfig.class)
public interface ContestSubmissionConfig {
    boolean getIsAllowedToViewAllSubmissions();
    Map<String, String> getUsernamesMap();
    List<String> getProblemJids();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableContestSubmissionConfig.Builder {}
}
