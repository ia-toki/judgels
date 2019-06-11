package judgels.jophiel.user.account;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.persistence.UserRegistrationEmailDao;
import judgels.jophiel.persistence.UserRegistrationEmailModel;
import judgels.service.RandomCodeGenerator;

public class UserRegistrationEmailStore {
    private final UserRegistrationEmailDao userRegistrationEmailDao;

    @Inject
    public UserRegistrationEmailStore(UserRegistrationEmailDao userRegistrationEmailDao) {
        this.userRegistrationEmailDao = userRegistrationEmailDao;
    }

    public boolean isUserActivated(String userJid) {
        return !userRegistrationEmailDao.selectByUserJid(userJid)
                .flatMap(model -> model.verified ? Optional.empty() : Optional.of(false))
                .isPresent();
    }

    public String generateEmailCode(String userJid) {
        UserRegistrationEmailModel model = new UserRegistrationEmailModel();
        model.userJid = userJid;
        model.emailCode = RandomCodeGenerator.newCode();
        userRegistrationEmailDao.insert(model);
        return model.emailCode;
    }

    public Optional<String> getEmailCode(String userJid) {
        return userRegistrationEmailDao.selectByUserJid(userJid).map(model -> model.emailCode);
    }

    public boolean verifyEmailCode(String emailCode) {
        return userRegistrationEmailDao.selectByEmailCode(emailCode).flatMap(model -> {
            if (model.verified) {
                return Optional.empty();
            }
            model.verified = true;
            userRegistrationEmailDao.update(model);
            return Optional.of(true);
        }).isPresent();
    }
}
