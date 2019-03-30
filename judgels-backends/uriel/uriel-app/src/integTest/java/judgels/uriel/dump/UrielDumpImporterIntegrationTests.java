package judgels.uriel.dump;

import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {
        AdminRoleModel.class,
        ContestModel.class,
        ContestAnnouncementModel.class
})
public class UrielDumpImporterIntegrationTests extends AbstractIntegrationTests {

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        createComponent(sessionFactory);
    }

    @Test
    void restore_admin_role() {

    }

    @Test
    void create_admin_role() {

    }

}
