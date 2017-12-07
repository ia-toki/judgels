package judgels.jophiel.user.email;

import java.util.Optional;
import javax.inject.Inject;

public class UserVerificationEmailStore {
    private final UserVerificationEmailDao userVerificationEmailDao;

    @Inject
    public UserVerificationEmailStore(UserVerificationEmailDao userVerificationEmailDao) {
        this.userVerificationEmailDao = userVerificationEmailDao;
    }

    public boolean isUserVerified(String userJid) {
        return !userVerificationEmailDao.findByUserJid(userJid)
                .flatMap(model -> model.verified ? Optional.empty() : Optional.of(false))
                .isPresent();
    }

    public String generateEmailCode(String userJid) {
        UserVerificationEmailModel model = new UserVerificationEmailModel();
        model.userJid = userJid;
        model.emailCode = EmailCodeGenerator.newCode();
        userVerificationEmailDao.insert(model);
        return model.emailCode;
    }

    public boolean verifyEmailCode(String emailCode) {
        return userVerificationEmailDao.findByEmailCode(emailCode).flatMap(model -> {
            if (model.verified) {
                return Optional.empty();
            }
            model.verified = true;
            userVerificationEmailDao.update(model);
            return Optional.of(true);
        }).isPresent();
    }
}
