package judgels.jophiel.user.password;

import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.user.email.EmailCodeGenerator;

public class UserForgotPasswordStore {
    private final UserForgotPasswordDao userForgotPasswordDao;

    @Inject
    public UserForgotPasswordStore(UserForgotPasswordDao userForgotPasswordDao) {
        this.userForgotPasswordDao = userForgotPasswordDao;
    }

    public String generateEmailCode(String userJid, Duration expiration) {
        Optional<UserForgotPasswordModel> maybeModel = userForgotPasswordDao.findUnusedByUserJid(userJid, expiration);
        if (maybeModel.isPresent()) {
            return maybeModel.get().emailCode;
        }

        UserForgotPasswordModel model = new UserForgotPasswordModel();
        model.userJid = userJid;
        model.emailCode = EmailCodeGenerator.newCode();
        userForgotPasswordDao.insert(model);
        return model.emailCode;
    }

    public Optional<String> consumeEmailCode(String emailCode) {
        return userForgotPasswordDao.findByEmailCode(emailCode).map(model -> {
            model.consumed = true;
            userForgotPasswordDao.update(model);
            return model.userJid;
        });
    }
}
