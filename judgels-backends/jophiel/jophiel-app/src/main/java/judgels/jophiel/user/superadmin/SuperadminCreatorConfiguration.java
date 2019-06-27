package judgels.jophiel.user.superadmin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSuperadminCreatorConfiguration.class)
public interface SuperadminCreatorConfiguration {
    SuperadminCreatorConfiguration DEFAULT = new SuperadminCreatorConfiguration.Builder()
            .initialPassword("superadmin")
            .build();

    String getInitialPassword();

    class Builder extends ImmutableSuperadminCreatorConfiguration.Builder {}
}
