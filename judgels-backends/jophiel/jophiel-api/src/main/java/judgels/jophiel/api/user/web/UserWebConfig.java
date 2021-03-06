package judgels.jophiel.api.user.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.role.UserRole;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserWebConfig.class)
public interface UserWebConfig {
    UserRole getRole();
    Profile getProfile();

    class Builder extends ImmutableUserWebConfig.Builder {}
}
