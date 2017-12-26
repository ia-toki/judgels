package judgels.jophiel.user.password;

import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.user.email.EmailCodeGenerator;

public class UserResetPasswordStore {
    private final UserResetPasswordDao userResetPasswordDao;

    @Inject
    public UserResetPasswordStore(UserResetPasswordDao userResetPasswordDao) {
        this.userResetPasswordDao = userResetPasswordDao;
    }

    public String generateEmailCode(String userJid, Duration expiration) {
        Optional<UserResetPasswordModel> maybeModel = userResetPasswordDao.findByUserJid(userJid, expiration);
        if (maybeModel.isPresent()) {
            return maybeModel.get().emailCode;
        }

        UserResetPasswordModel model = new UserResetPasswordModel();
        model.userJid = userJid;
        model.emailCode = EmailCodeGenerator.newCode();
        userResetPasswordDao.insert(model);
        return model.emailCode;
    }

    public Optional<String> consumeEmailCode(String emailCode, Duration expiration) {
        return userResetPasswordDao.findByEmailCode(emailCode, expiration).map(model -> {
            model.consumed = true;
            userResetPasswordDao.update(model);
            return model.userJid;
        });
    }
}
