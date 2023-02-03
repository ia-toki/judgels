package judgels.jophiel.user.account;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import java.util.Optional;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.account.GoogleUserRegistrationData;
import judgels.jophiel.api.user.account.UserRegistrationData;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.auth.google.GoogleAuth;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.recaptcha.RecaptchaVerifier;

public class UserRegisterer {
    private final UserStore userStore;
    private final UserInfoStore userInfoStore;
    private final UserRegistrationEmailStore userRegistrationEmailStore;
    private final UserRegistrationEmailMailer userRegistrationEmailMailer;
    private final Optional<RecaptchaVerifier> recaptchaVerifier;
    private final Optional<GoogleAuth> googleAuth;

    public UserRegisterer(
            UserStore userStore,
            UserInfoStore userInfoStore,
            UserRegistrationEmailStore userRegistrationEmailStore,
            UserRegistrationEmailMailer userRegistrationEmailMailer,
            Optional<RecaptchaVerifier> recaptchaVerifier,
            Optional<GoogleAuth> googleAuth) {
        this.userStore = userStore;
        this.userInfoStore = userInfoStore;
        this.userRegistrationEmailStore = userRegistrationEmailStore;
        this.userRegistrationEmailMailer = userRegistrationEmailMailer;
        this.recaptchaVerifier = recaptchaVerifier;
        this.googleAuth = googleAuth;
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

        UserInfo userInfo = new UserInfo.Builder()
                .name(data.getName())
                .build();
        userInfoStore.upsertInfo(user.getJid(), userInfo);

        String emailCode = userRegistrationEmailStore.generateEmailCode(user.getJid());
        userRegistrationEmailMailer.sendActivationEmail(user, data.getEmail(), emailCode);

        return user;
    }

    public User registerGoogleUser(GoogleUserRegistrationData data) {
        if (!googleAuth.isPresent()) {
            throw new ForbiddenException();
        }
        GoogleIdToken.Payload payload = googleAuth.get().verifyIdToken(data.getIdToken());

        User user = userStore.createUser(new UserData.Builder()
                .username(data.getUsername())
                .email(payload.getEmail())
                .build());

        if (payload.get("name") != null) {
            userInfoStore.upsertInfo(user.getJid(), new UserInfo.Builder()
                    .name((String) payload.get("name"))
                    .build());
        }

        return user;
    }

    public void activate(String emailCode) {
        if (!userRegistrationEmailStore.verifyEmailCode(emailCode)) {
            throw new NotFoundException();
        }
    }

    public void resendActivationEmail(User user) {
        Optional<String> maybeEmailCode = userRegistrationEmailStore.getEmailCode(user.getJid());
        String emailCode = maybeEmailCode.orElseGet(() -> userRegistrationEmailStore.generateEmailCode(user.getJid()));
        userRegistrationEmailMailer.sendActivationEmail(user, user.getEmail(), emailCode);
    }
}
