package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserInfo.class)
public interface UserInfo {
    String getUsername();
    Optional<Integer> getRating();

    class Builder extends ImmutableUserInfo.Builder {}
}
