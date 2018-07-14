package judgels.uriel.api.contest.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.user.UserInfo;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.Submission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSubmissionsResponse.class)
public interface ContestSubmissionsResponse {
    Page<Submission> getData();
    Map<String, UserInfo> getUsersMap();
    Map<String, String> getProblemAliasesMap();

    class Builder extends ImmutableContestSubmissionsResponse.Builder {}
}
