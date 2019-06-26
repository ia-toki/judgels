package judgels.jophiel.user.superadmin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSuperadminCreatorConfiguration.class)
public interface SuperadminCreatorConfiguration {
    SuperadminCreatorConfiguration DEFAULT = new SuperadminCreatorConfiguration.Builder()
            .enabled(true)
            .username("superadmin")
            .initialPassword("superadmin")
            .initialEmail("superadmin@jophiel.judgels")
            .build();

    boolean getEnabled();
    Optional<String> getUsername();
    Optional<String> getInitialPassword();
    Optional<String> getInitialEmail();

    class Builder extends ImmutableSuperadminCreatorConfiguration.Builder {}
}
