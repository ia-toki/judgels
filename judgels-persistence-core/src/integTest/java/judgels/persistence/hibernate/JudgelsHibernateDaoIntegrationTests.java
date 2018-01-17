package judgels.persistence.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.ActorProvider;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.JidPrefix;
import judgels.persistence.JudgelsModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {JudgelsHibernateDaoIntegrationTests.ExampleModel.class})
class JudgelsHibernateDaoIntegrationTests {
    @Test void can_do_basic_crud(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());

        assertThat(dao.select(1)).isEmpty();

        ExampleModel model = new ExampleModel();
        model.column = "value1";
        dao.insert(model);

        model = dao.select(1).get();

        assertThat(model.id).isEqualTo(1);
        assertThat(model.jid).startsWith("JID");
        assertThat(model.column).isEqualTo("value1");

        String jid = model.jid;

        model = dao.selectByJid(jid).get();

        assertThat(model.id).isEqualTo(1);
        assertThat(model.jid).isEqualTo(jid);
        assertThat(model.column).isEqualTo("value1");

        model = new ExampleModel();
        model.column = "value3";
        dao.insert(model);

        model = dao.selectByJid(jid).get();

        model.column = "value2";
        model = dao.updateByJid(jid, model);

        assertThat(model.id).isEqualTo(1);
        assertThat(model.jid).isEqualTo(jid);
        assertThat(model.column).isEqualTo("value2");

        model = dao.select(2).get();

        assertThat(model.jid).isNotEqualTo(jid);
        assertThat(model.column).isEqualTo("value3");

        assertThat(dao.selectByJid("JIDEXAMnotfound")).isEmpty();
    }

    @Entity
    @JidPrefix("EXAM")
    static class ExampleModel extends JudgelsModel {
        @Column
        String column;
    }

    private static class ExampleHibernateDao extends JudgelsHibernateDao<ExampleModel> {
        ExampleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }
    }
}
