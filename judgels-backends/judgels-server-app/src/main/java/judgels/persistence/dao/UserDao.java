package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.model.UserModel;
import judgels.persistence.model.UserModel_;

public class UserDao extends JudgelsDao<UserModel> {
    @Inject
    public UserDao(DaoData data) {
        super(data);
    }

    public Optional<UserModel> selectByUsername(String username) {
        return select().where(columnEq(UserModel_.username, username)).unique();
    }

    public Optional<UserModel> selectByEmail(String email) {
        return select().where(columnEq(UserModel_.email, email)).unique();
    }

    public List<UserModel> selectAllByUsernames(Collection<String> usernames) {
        return select().where(columnIn(UserModel_.username, usernames)).all();
    }
}
