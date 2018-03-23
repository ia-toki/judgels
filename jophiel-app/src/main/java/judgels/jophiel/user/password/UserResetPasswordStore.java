package judgels.jophiel.user.password;

import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.persistence.Daos.UserResetPasswordDao;
import judgels.jophiel.persistence.UserResetPasswordModel;
import judgels.jophiel.persistence.UserResetPasswordRawDao;
import judgels.service.RandomCodeGenerator;

public class UserResetPasswordStore {
    private final UserResetPasswordDao userResetPasswordDao;
    private final UserResetPasswordRawDao userResetPasswordRawDao;

    @Inject
    public UserResetPasswordStore(
            UserResetPasswordDao userResetPasswordDao,
            UserResetPasswordRawDao userResetPasswordRawDao) {
        this.userResetPasswordDao = userResetPasswordDao;
        this.userResetPasswordRawDao = userResetPasswordRawDao;
    }

    public String generateEmailCode(String userJid, Duration expiration) {
        Optional<UserResetPasswordModel> maybeModel = userResetPasswordRawDao.selectByUserJid(userJid, expiration);
        if (maybeModel.isPresent()) {
            return maybeModel.get().emailCode;
        }

        UserResetPasswordModel model = new UserResetPasswordModel();
        model.userJid = userJid;
        model.emailCode = RandomCodeGenerator.newCode();
        userResetPasswordDao.insert(model);
        return model.emailCode;
    }

    public Optional<String> consumeEmailCode(String emailCode, Duration expiration) {
        return userResetPasswordRawDao.selectByEmailCode(emailCode, expiration).map(model -> {
            model.consumed = true;
            userResetPasswordDao.update(model);
            return model.userJid;
        });
    }
}
