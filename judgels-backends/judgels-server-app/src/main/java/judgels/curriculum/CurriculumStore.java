package judgels.curriculum;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.api.curriculum.Curriculum;
import judgels.persistence.dao.CurriculumDao;
import judgels.persistence.model.CurriculumModel;

public class CurriculumStore {
    private final CurriculumDao curriculumDao;

    @Inject
    public CurriculumStore(CurriculumDao curriculumDao) {
        this.curriculumDao = curriculumDao;
    }

    /**
     * Currently, only one curriculum is supported.
     */
    public Optional<Curriculum> getCurriculum() {
        List<Curriculum> curriculums = getCurriculums();
        if (curriculums.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(curriculums.get(0));
    }

    public List<Curriculum> getCurriculums() {
        return Lists.transform(
                curriculumDao.select().all(),
                CurriculumStore::fromModel);
    }

    private static Curriculum fromModel(CurriculumModel m) {
        return new Curriculum.Builder()
                .name(m.name)
                .description(m.description)
                .build();
    }
}
