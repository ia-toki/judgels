package judgels.training.account.registration;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.dao.AccountRegistrationEmailDao;
import judgels.persistence.model.AccountRegistrationEmailModel;
import judgels.user.RandomCodeGenerator;

public class AccountRegistrationEmailStore {
    private final AccountRegistrationEmailDao accountRegistrationEmailDao;

    @Inject
    public AccountRegistrationEmailStore(AccountRegistrationEmailDao accountRegistrationEmailDao) {
        this.accountRegistrationEmailDao = accountRegistrationEmailDao;
    }

    public String generateEmailCode(String userJid) {
        AccountRegistrationEmailModel model = new AccountRegistrationEmailModel();
        model.userJid = userJid;
        model.emailCode = RandomCodeGenerator.newCode();
        accountRegistrationEmailDao.insert(model);
        return model.emailCode;
    }

    public Optional<String> getEmailCode(String userJid) {
        return accountRegistrationEmailDao.selectByUserJid(userJid).map(model -> model.emailCode);
    }

    public boolean verifyEmailCode(String emailCode) {
        return accountRegistrationEmailDao.selectByEmailCode(emailCode).flatMap(model -> {
            if (model.verified) {
                return Optional.empty();
            }
            model.verified = true;
            accountRegistrationEmailDao.update(model);
            return Optional.of(true);
        }).isPresent();
    }
}
