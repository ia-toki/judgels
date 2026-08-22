package tlx.curriculum;

import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurriculumCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurriculumCreator.class);

    private final CurriculumStore curriculumStore;

    public CurriculumCreator(CurriculumStore curriculumStore) {
        this.curriculumStore = curriculumStore;
    }

    @UnitOfWork
    public void ensureCurriculumExists() {
        if (curriculumStore.getCurriculum().isPresent()) {
            return;
        }
        curriculumStore.createCurriculum("Curriculum", "<p>Curriculum description</p>");
        LOGGER.info("Created initial curriculum");
    }
}
