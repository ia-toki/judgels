package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.AccountRegistrationEmailModel;
import judgels.persistence.model.AccountRegistrationEmailModel_;

public class AccountRegistrationEmailDao extends Dao<AccountRegistrationEmailModel> {
    @Inject
    public AccountRegistrationEmailDao(DaoData data) {
        super(data);
    }

    public Optional<AccountRegistrationEmailModel> selectByUserJid(String userJid) {
        return select().where(columnEq(AccountRegistrationEmailModel_.userJid, userJid)).unique();
    }

    public Optional<AccountRegistrationEmailModel> selectByEmailCode(String emailCode) {
        return select().where(columnEq(AccountRegistrationEmailModel_.emailCode, emailCode)).unique();
    }
}
