package judgels.jophiel.api.user.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.jophiel.api.user.User;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUsersDump.class)
public interface UsersDump {
    List<User> getUsers();

    class Builder extends ImmutableUsersDump.Builder {}
}
