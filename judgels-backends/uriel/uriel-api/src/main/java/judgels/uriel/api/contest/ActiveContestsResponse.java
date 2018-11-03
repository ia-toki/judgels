package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableActiveContestsResponse.class)
public interface ActiveContestsResponse {
    List<Contest> getData();

    class Builder extends ImmutableActiveContestsResponse.Builder {}
}
