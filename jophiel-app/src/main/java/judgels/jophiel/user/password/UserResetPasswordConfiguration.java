package judgels.jophiel.user.password;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserResetPasswordConfiguration.class)
public interface UserResetPasswordConfiguration {
    UserResetPasswordConfiguration DEFAULT = new UserResetPasswordConfiguration.Builder()
            .enabled(false)
            .build();

    boolean getEnabled();

    class Builder extends ImmutableUserResetPasswordConfiguration.Builder {}
}
