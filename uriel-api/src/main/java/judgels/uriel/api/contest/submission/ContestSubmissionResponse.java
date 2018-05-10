package judgels.uriel.api.contest.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.user.UserInfo;
import judgels.sandalphon.api.submission.Submission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionsResponse.class)
public interface ContestSubmissionResponse {
    Submission getData();
    UserInfo getUser();
    String getProblemName();
    String getProblemAlias();
    String getContestName();

    class Builder extends ImmutableContestSubmissionResponse.Builder {}
}
