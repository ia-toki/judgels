package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.model.CurriculumModel;

public class CurriculumDao extends JudgelsDao<CurriculumModel> {
    @Inject
    public CurriculumDao(DaoData data) {
        super(data);
    }
}
