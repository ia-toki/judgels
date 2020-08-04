package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersUpsertResponse.class)
public interface UsersUpsertResponse {
    List<String> getCreatedUsernames();
    List<String> getUpdatedUsernames();

    class Builder extends ImmutableUsersUpsertResponse.Builder {}
}
