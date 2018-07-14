package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.user.UserInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionWithSourceResponse.class)
public interface SubmissionWithSourceResponse {
    SubmissionWithSource getData();
    UserInfo getUser();
    String getProblemName();
    String getProblemAlias();
    String getContainerName();

    class Builder extends ImmutableSubmissionWithSourceResponse.Builder {}
}
