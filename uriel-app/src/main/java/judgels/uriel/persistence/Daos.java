package judgels.uriel.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.UnmodifiableDao;

public class Daos {
    private Daos() {}

    public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {}

    public interface ContestDao extends JudgelsDao<ContestModel> {}
}
