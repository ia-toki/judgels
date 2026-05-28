package judgels.session;

import judgels.api.user.User;

public interface SessionLoginValidator {
    void validate(User user);
}
