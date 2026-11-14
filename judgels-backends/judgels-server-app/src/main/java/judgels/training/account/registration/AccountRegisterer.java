package judgels.training.account.registration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.util.Optional;
import judgels.api.training.account.registration.AccountRegistrationData;
import judgels.api.training.account.registration.AccountRegistrationErrors;
import judgels.api.training.account.registration.GoogleAccountRegistrationData;
import judgels.api.user.User;
import judgels.api.user.UserData;
import judgels.api.user.info.UserInfo;
import judgels.core.auth.google.GoogleAuth;
import judgels.core.recaptcha.RecaptchaVerifier;
import judgels.setting.TrainingAccountChecker;
import judgels.user.UserStore;
import judgels.user.info.UserInfoStore;

public class AccountRegisterer {
    private final UserStore userStore;
    private final UserInfoStore userInfoStore;
    private final AccountRegistrationEmailStore accountRegistrationEmailStore;
    private final AccountRegistrationEmailMailer accountRegistrationEmailMailer;
    private final TrainingAccountChecker trainingAccountChecker;
    private final Optional<RecaptchaVerifier> recaptchaVerifier;
    private final Optional<GoogleAuth> googleAuth;

    public AccountRegisterer(
            UserStore userStore,
            UserInfoStore userInfoStore,
            AccountRegistrationEmailStore accountRegistrationEmailStore,
            AccountRegistrationEmailMailer accountRegistrationEmailMailer,
            TrainingAccountChecker trainingAccountChecker,
            Optional<RecaptchaVerifier> recaptchaVerifier,
            Optional<GoogleAuth> googleAuth) {
        this.userStore = userStore;
        this.userInfoStore = userInfoStore;
        this.accountRegistrationEmailStore = accountRegistrationEmailStore;
        this.accountRegistrationEmailMailer = accountRegistrationEmailMailer;
        this.trainingAccountChecker = trainingAccountChecker;
        this.recaptchaVerifier = recaptchaVerifier;
        this.googleAuth = googleAuth;
    }

    public User register(AccountRegistrationData data) {
        if (trainingAccountChecker.isRegistrationRecaptchaEnabled()) {
            if (!data.getRecaptchaResponse().isPresent()) {
                throw new IllegalArgumentException("Missing recaptcha response");
            }
            if (!recaptchaVerifier.get().verify(data.getRecaptchaResponse().get())) {
                throw new IllegalArgumentException("Invalid recaptcha response");
            }
        }

        checkUsernameAvailable(data.getUsername());
        checkEmailAvailable(data.getEmail());

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

        String emailCode = accountRegistrationEmailStore.generateEmailCode(user.getJid());
        accountRegistrationEmailMailer.sendActivationEmail(user, data.getEmail(), emailCode);

        return user;
    }

    public User registerGoogleUser(GoogleAccountRegistrationData data) {
        if (!googleAuth.isPresent()) {
            throw new ForbiddenException();
        }
        GoogleIdToken.Payload payload = googleAuth.get().verifyIdToken(data.getIdToken());
        checkUsernameAvailable(data.getUsername());
        checkEmailAvailable(payload.getEmail());

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
        if (!accountRegistrationEmailStore.verifyEmailCode(emailCode)) {
            throw new NotFoundException();
        }
    }

    public void resendActivationEmail(User user) {
        Optional<String> maybeEmailCode = accountRegistrationEmailStore.getEmailCode(user.getJid());
        String emailCode = maybeEmailCode.orElseGet(() -> accountRegistrationEmailStore.generateEmailCode(user.getJid()));
        accountRegistrationEmailMailer.sendActivationEmail(user, user.getEmail(), emailCode);
    }

    private void checkUsernameAvailable(String username) {
        if (userStore.getUserByUsername(username).isPresent()) {
            throw AccountRegistrationErrors.usernameAlreadyExists();
        }
    }

    private void checkEmailAvailable(String email) {
        if (userStore.getUserByEmail(email).isPresent()) {
            throw AccountRegistrationErrors.emailAlreadyExists();
        }
    }
}
