package judgels.jophiel.persistence;

import judgels.persistence.Dao;
import judgels.persistence.UnmodifiableDao;

public class Daos {
    private Daos() {}

    public interface SessionDao extends UnmodifiableDao<SessionModel> {}

    public interface UserProfileDao extends Dao<UserProfileModel> {}
}
