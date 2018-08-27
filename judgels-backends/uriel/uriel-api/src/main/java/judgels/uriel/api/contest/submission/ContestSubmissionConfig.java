package judgels.uriel.api.contest.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionConfig.class)
public interface ContestSubmissionConfig {
    boolean getIsAllowedToViewAllSubmissions();

    class Builder extends ImmutableContestSubmissionConfig.Builder {}
}
