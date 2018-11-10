package judgels.jophiel.user.account;

import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.persistence.UserResetPasswordDao;
import judgels.jophiel.persistence.UserResetPasswordModel;
import judgels.service.RandomCodeGenerator;

public class UserResetPasswordStore {
    private final UserResetPasswordDao userResetPasswordDao;

    @Inject
    public UserResetPasswordStore(UserResetPasswordDao userResetPasswordDao) {
        this.userResetPasswordDao = userResetPasswordDao;
    }

    public String generateEmailCode(String userJid, Duration expiration) {
        Optional<UserResetPasswordModel> maybeModel = userResetPasswordDao.selectByUserJid(userJid, expiration);
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
        return userResetPasswordDao.selectByEmailCode(emailCode, expiration).map(model -> {
            model.consumed = true;
            userResetPasswordDao.update(model);
            return model.userJid;
        });
    }
}
