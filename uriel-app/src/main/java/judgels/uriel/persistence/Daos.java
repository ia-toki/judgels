package judgels.uriel.persistence;

import judgels.persistence.Dao;
import judgels.persistence.JudgelsDao;
import judgels.persistence.UnmodifiableDao;

public class Daos {
    private Daos() {}

    public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {}

    public interface ContestDao extends JudgelsDao<ContestModel> {}

    public interface ContestContestantDao extends Dao<ContestContestantModel> {}

    public interface ContestScoreboardDao extends Dao<ContestScoreboardModel> {}
}
