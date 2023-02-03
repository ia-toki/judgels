package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableImportContestsDumpResponse.class)
public interface ImportContestsDumpResponse {
    Set<String> getCreatedContestJids();

    class Builder extends ImmutableImportContestsDumpResponse.Builder {}
}
