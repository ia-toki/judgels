package judgels.training.account.password;

import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Optional;
import judgels.persistence.dao.AccountPasswordResetDao;
import judgels.persistence.model.AccountPasswordResetModel;
import judgels.user.RandomCodeGenerator;

public class AccountPasswordResetStore {
    private final AccountPasswordResetDao accountPasswordResetDao;

    @Inject
    public AccountPasswordResetStore(AccountPasswordResetDao accountPasswordResetDao) {
        this.accountPasswordResetDao = accountPasswordResetDao;
    }

    public String generateEmailCode(String userJid, Duration expiration) {
        Optional<AccountPasswordResetModel> maybeModel = accountPasswordResetDao.selectByUserJid(userJid, expiration);
        if (maybeModel.isPresent()) {
            return maybeModel.get().emailCode;
        }

        AccountPasswordResetModel model = new AccountPasswordResetModel();
        model.userJid = userJid;
        model.emailCode = RandomCodeGenerator.newCode();
        accountPasswordResetDao.insert(model);
        return model.emailCode;
    }

    public Optional<String> consumeEmailCode(String emailCode, Duration expiration) {
        return accountPasswordResetDao.selectByEmailCode(emailCode, expiration).map(model -> {
            model.consumed = true;
            accountPasswordResetDao.update(model);
            return model.userJid;
        });
    }
}
