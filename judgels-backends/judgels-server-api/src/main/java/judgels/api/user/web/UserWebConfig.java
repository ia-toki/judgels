package judgels.api.user.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.api.profile.Profile;
import judgels.api.user.role.UserRole;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserWebConfig.class)
public interface UserWebConfig {
    String getAppName();
    String getAppSlogan();
    Optional<String> getAppAnnouncement();
    String getHomeBanner();
    UserRole getRole();
    Optional<Profile> getProfile();

    class Builder extends ImmutableUserWebConfig.Builder {}
}
