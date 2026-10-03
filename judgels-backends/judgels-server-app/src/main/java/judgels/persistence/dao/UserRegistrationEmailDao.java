package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.DaoData;
import judgels.persistence.model.UserRegistrationEmailModel;
import judgels.persistence.model.UserRegistrationEmailModel_;

public class UserRegistrationEmailDao extends Dao<UserRegistrationEmailModel> {
    @Inject
    public UserRegistrationEmailDao(DaoData data) {
        super(data);
    }

    public Optional<UserRegistrationEmailModel> selectByUserJid(String userJid) {
        return select().where(columnEq(UserRegistrationEmailModel_.userJid, userJid)).unique();
    }

    public Optional<UserRegistrationEmailModel> selectByEmailCode(String emailCode) {
        return select().where(columnEq(UserRegistrationEmailModel_.emailCode, emailCode)).unique();
    }
}
