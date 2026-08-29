package tlx.curriculum;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import judgels.persistence.dao.CurriculumDao;
import judgels.persistence.model.CurriculumModel;
import tlx.api.curriculum.Curriculum;
import tlx.api.curriculum.CurriculumUpdateData;

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

    public Optional<Curriculum> getCurriculumByJid(String curriculumJid) {
        return curriculumDao.selectByJid(curriculumJid).map(CurriculumStore::fromModel);
    }

    public Curriculum createCurriculum(String name, String description) {
        CurriculumModel model = new CurriculumModel();
        model.name = name;
        model.description = description;
        return fromModel(curriculumDao.insert(model));
    }

    public Curriculum updateCurriculum(String curriculumJid, CurriculumUpdateData data) {
        CurriculumModel model = curriculumDao.findByJid(curriculumJid);
        data.getName().ifPresent(name -> model.name = name);
        data.getDescription().ifPresent(description -> model.description = description);
        return fromModel(curriculumDao.update(model));
    }

    private static Curriculum fromModel(CurriculumModel m) {
        return new Curriculum.Builder()
                .jid(m.jid)
                .name(m.name)
                .description(m.description)
                .build();
    }
}
