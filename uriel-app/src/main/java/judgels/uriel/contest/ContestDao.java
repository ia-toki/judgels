package judgels.uriel.contest;

import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;

public interface ContestDao extends JudgelsDao<ContestModel> {
    /**
     * @deprecated This is a temporary workaround before role authorization is implemented.
     */
    @Deprecated
    long selectCountPublic();

    /**
     * @deprecated This is a temporary workaround before role authorization is implemented.
     */
    @Deprecated
    Page<ContestModel> selectAllPublic(int page, int pageSize);
}
