package judgels.jophiel.persistence;

import judgels.persistence.Dao;
import judgels.persistence.JudgelsDao;
import judgels.persistence.UnmodifiableDao;

public class Daos {
    private Daos() {}

    public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {}

    public interface SessionDao extends UnmodifiableDao<SessionModel> {}

    public interface UserDao extends JudgelsDao<UserModel> {}

    public interface UserProfileDao extends Dao<UserProfileModel> {}

    public interface UserRegistrationEmailDao extends Dao<UserRegistrationEmailModel> {}

    public interface UserResetPasswordDao extends Dao<UserResetPasswordModel> {}
}
