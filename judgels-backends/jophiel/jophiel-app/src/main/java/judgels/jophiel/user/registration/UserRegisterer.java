package judgels.jophiel.user.registration;

import java.util.Optional;
import javax.ws.rs.NotFoundException;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.profile.UserProfile;
import judgels.jophiel.api.user.registration.UserRegistrationData;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.profile.UserProfileStore;
import judgels.recaptcha.RecaptchaVerifier;

public class UserRegisterer {
    private final UserStore userStore;
    private final UserProfileStore userProfileStore;
    private final UserRegistrationEmailStore userRegistrationEmailStore;
    private final UserRegistrationEmailMailer userRegistrationEmailMailer;
    private final Optional<RecaptchaVerifier> recaptchaVerifier;

    public UserRegisterer(
            UserStore userStore,
            UserProfileStore userProfileStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            UserRegistrationEmailMailer userRegistrationEmailMailer,
            Optional<RecaptchaVerifier> recaptchaVerifier) {
        this.userStore = userStore;
        this.userProfileStore = userProfileStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
        this.userRegistrationEmailMailer = userRegistrationEmailMailer;
        this.recaptchaVerifier = recaptchaVerifier;
    }

    public User register(UserRegistrationData data) {
        if (recaptchaVerifier.isPresent()) {
            if (!data.getRecaptchaResponse().isPresent()) {
                throw new IllegalArgumentException("Missing recaptcha response");
            }
            if (!recaptchaVerifier.get().verify(data.getRecaptchaResponse().get())) {
                throw new IllegalArgumentException("Invalid recaptcha response");
            }
        }

        UserData userData = new UserData.Builder()
                .username(data.getUsername())
                .password(data.getPassword())
                .email(data.getEmail())
                .build();
        User user = userStore.createUser(userData);

        UserProfile userProfile = new UserProfile.Builder()
                .name(data.getName())
                .build();
        userProfileStore.upsertProfile(user.getJid(), userProfile);

        String emailCode = userRegistrationEmailStore.generateEmailCode(user.getJid());
        userRegistrationEmailMailer.sendActivationEmail(user, data.getEmail(), emailCode);

        return user;
    }

    public void activate(String emailCode) {
        if (!userRegistrationEmailStore.verifyEmailCode(emailCode)) {
            throw new NotFoundException();
        }
    }
}
