package judgels.jerahmeel.curriculum;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import judgels.jerahmeel.api.curriculum.CurriculumService;
import judgels.jerahmeel.api.curriculum.CurriculumsResponse;

public class CurriculumResource implements CurriculumService {
    private final CurriculumStore curriculumStore;

    @Inject
    public CurriculumResource(CurriculumStore curriculumStore) {
        this.curriculumStore = curriculumStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public CurriculumsResponse getCurriculums() {
        return new CurriculumsResponse.Builder()
                .data(curriculumStore.getCurriculums())
                .build();
    }
}
