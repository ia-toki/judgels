package judgels.persistence.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.Map;
import java.util.Set;
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
    @Test
    void can_do_basic_crud(SessionFactory sessionFactory) {
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

    @Test
    void can_select_multiple_jids(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());

        ExampleModel model1 = new ExampleModel();
        model1.column = "value4";
        model1 = dao.insert(model1);

        ExampleModel model2 = new ExampleModel();
        model2.column = "value4";
        model2 = dao.insert(model2);

        Set<String> jids = ImmutableSet.of(model1.jid, model2.jid);
        Map<String, ExampleModel> models = dao.selectByJids(jids);
        assertThat(models).hasSize(2);

        assertThat(models).containsKeys(model1.jid, model2.jid);
        assertThat(models.get(model1.jid)).isEqualTo(model1);
        assertThat(models.get(model2.jid)).isEqualTo(model2);

        // assert to only return found jids
        jids = ImmutableSet.of(model1.jid, "1234");
        models = dao.selectByJids(jids);
        assertThat(models).hasSize(1);
        assertThat(models).containsKey(model1.jid);
        assertThat(models.get(model1.jid)).isEqualTo(model1);

        // assert to ignore empty list
        jids = ImmutableSet.of();
        models = dao.selectByJids(jids);
        assertThat(models).isEmpty();
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
