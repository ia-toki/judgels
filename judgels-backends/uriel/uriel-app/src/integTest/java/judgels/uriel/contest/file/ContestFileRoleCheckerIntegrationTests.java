package judgels.uriel.contest.file;

import judgels.uriel.contest.role.AbstractRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;

class ContestFileRoleCheckerIntegrationTests extends AbstractRoleCheckerIntegrationTests {
    private ContestFileRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestFileRoleChecker();
    }
}
