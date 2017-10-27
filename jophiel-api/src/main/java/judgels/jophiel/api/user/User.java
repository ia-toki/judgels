package judgels.jophiel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUser.class)
public interface User extends UserBase {
    long getId();
    String getJid();

    class Builder extends ImmutableUser.Builder {}

    @Value.Immutable
    @JsonDeserialize(as = ImmutableData.class)
    interface Data extends UserBase {
        class Builder extends ImmutableData.Builder {}
    }
}
