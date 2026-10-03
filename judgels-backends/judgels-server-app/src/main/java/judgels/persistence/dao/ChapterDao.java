package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.model.ChapterModel;

public class ChapterDao extends JudgelsDao<ChapterModel> {
    @Inject
    public ChapterDao(DaoData data) {
        super(data);
    }
}
