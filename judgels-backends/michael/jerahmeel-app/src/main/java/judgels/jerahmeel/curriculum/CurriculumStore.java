package judgels.jerahmeel.curriculum;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.curriculum.Curriculum;
import judgels.jerahmeel.persistence.CurriculumDao;
import judgels.jerahmeel.persistence.CurriculumModel;
import judgels.persistence.api.SelectionOptions;

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
                curriculumDao.selectAll(SelectionOptions.DEFAULT_ALL),
                CurriculumStore::fromModel);
    }

    private static Curriculum fromModel(CurriculumModel m) {
        return new Curriculum.Builder()
                .name(m.name)
                .description(m.description)
                .build();
    }
}
