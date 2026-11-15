package judgels.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersImportResponse.class)
public interface UsersImportResponse {
    List<String> getCreatedUsernames();
    List<String> getUpdatedUsernames();

    class Builder extends ImmutableUsersImportResponse.Builder {}
}
