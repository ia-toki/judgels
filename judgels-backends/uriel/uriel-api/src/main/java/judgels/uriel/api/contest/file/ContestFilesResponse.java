package judgels.uriel.api.contest.file;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestFilesResponse.class)
public interface ContestFilesResponse {
    List<ContestFile> getData();
    ContestFileConfig getConfig();

    class Builder extends ImmutableContestFilesResponse.Builder {}
}
