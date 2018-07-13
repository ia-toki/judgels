package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserInfo.class)
public interface UserInfo {
    String getUsername();

    class Builder extends ImmutableUserInfo.Builder {}
}
