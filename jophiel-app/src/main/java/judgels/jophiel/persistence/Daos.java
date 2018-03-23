package judgels.jophiel.persistence;

import judgels.persistence.Dao;
import judgels.persistence.UnmodifiableDao;

public class Daos {
    private Daos() {}

    public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {}

    public interface SessionDao extends UnmodifiableDao<SessionModel> {}

    public interface UserProfileDao extends Dao<UserProfileModel> {}

    public interface UserRegistrationEmailDao extends Dao<UserRegistrationEmailModel> {}
}
