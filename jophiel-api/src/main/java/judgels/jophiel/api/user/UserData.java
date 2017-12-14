package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserData.class)
public interface UserData {
    String getUsername();
    String getPassword();
    String getName();
    String getEmail();

    class Builder extends ImmutableUserData.Builder {}
}
