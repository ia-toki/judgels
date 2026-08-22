package tlx.curriculum;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.persistence.hibernate.WithHibernateSession;
import judgels.persistence.model.CurriculumModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tlx.api.curriculum.Curriculum;
import tlx.training.BaseTrainingIntegrationTests;
import tlx.training.TrainingIntegrationTestComponent;

@WithHibernateSession(models = {CurriculumModel.class})
public class CurriculumCreatorIntegrationTests extends BaseTrainingIntegrationTests {
    private CurriculumStore curriculumStore;
    private CurriculumCreator curriculumCreator;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        TrainingIntegrationTestComponent component = createComponent(sessionFactory);
        curriculumStore = component.curriculumStore();
        curriculumCreator = new CurriculumCreator(curriculumStore);
    }

    @Test
    void ensure_curriculum_exists() {
        // initially, there is no curriculum
        assertThat(curriculumStore.getCurriculum()).isEmpty();

        curriculumCreator.ensureCurriculumExists();

        // now, a curriculum exists
        Curriculum curriculum = curriculumStore.getCurriculum().get();
        assertThat(curriculum.getName()).isEqualTo("Curriculum");

        curriculumCreator.ensureCurriculumExists();

        // no new curriculum is created
        assertThat(curriculumStore.getCurriculums()).hasSize(1);
    }
}
