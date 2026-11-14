package judgels.user.superadmin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSuperadminConfiguration.class)
public interface SuperadminConfiguration {
    SuperadminConfiguration DEFAULT = new SuperadminConfiguration.Builder()
            .initialPassword("superadmin")
            .build();

    String getInitialPassword();

    class Builder extends ImmutableSuperadminConfiguration.Builder {}
}
