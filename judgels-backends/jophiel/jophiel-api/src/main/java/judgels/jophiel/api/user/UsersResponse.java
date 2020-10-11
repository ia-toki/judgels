package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Map;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersResponse.class)
public interface UsersResponse {
    Page<User> getData();
    Map<String, Instant> getLastSessionTimesMap();

    class Builder extends ImmutableUsersResponse.Builder {}
}
