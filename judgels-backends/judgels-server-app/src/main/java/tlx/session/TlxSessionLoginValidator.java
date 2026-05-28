package tlx.session;

import java.util.Optional;
import judgels.api.user.User;
import judgels.session.SessionLoginValidator;
import tlx.api.session.SessionWithRegistrationErrors;
import tlx.user.registration.UserRegistrationConfiguration;
import tlx.user.registration.UserRegistrationEmailStore;

public class TlxSessionLoginValidator implements SessionLoginValidator {
    private final Optional<UserRegistrationConfiguration> userRegistrationConfig;
    private final UserRegistrationEmailStore userRegistrationEmailStore;

    public TlxSessionLoginValidator(
            Optional<UserRegistrationConfiguration> userRegistrationConfig,
            UserRegistrationEmailStore userRegistrationEmailStore) {
        this.userRegistrationConfig = userRegistrationConfig;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
    }

    @Override
    public void validate(User user) {
        if (userRegistrationConfig.isPresent()) {
            if (!userRegistrationEmailStore.isUserActivated(user.getJid())) {
                throw SessionWithRegistrationErrors.userNotActivated(user.getEmail());
            }
        }
    }
}
