package judgels.jerahmeel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserTopStats.class)
public interface UserTopStats {
    List<UserTopStatsEntry> getTopUsers();

    class Builder extends ImmutableUserTopStats.Builder {}
}
