package judgels.persistence.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import judgels.persistence.ActorProvider;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.Page;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ExampleModel.class})
class HibernateDaoIntegrationTests {
    @Test void can_do_basic_crud(SessionFactory sessionFactory) {
        Date now1 = new Date(42);
        ExampleHibernateDao dao1 = new ExampleHibernateDao(
                sessionFactory,
                new FixedClock(42),
                new FixedActorProvider("actor1", "ip1"));

        assertThat(dao1.select(1)).isEmpty();

        ExampleModel model1 = new ExampleModel();
        model1.column = "value1";
        dao1.insert(model1);

        Optional<ExampleModel> maybeModel = dao1.select(1);
        assertThat(maybeModel).isPresent();
        model1 = maybeModel.get();

        assertThat(model1.id).isEqualTo(1);
        assertThat(model1.createdBy).isEqualTo("actor1");
        assertThat(model1.createdAt).isEqualTo(now1);
        assertThat(model1.createdIp).isEqualTo("ip1");
        assertThat(model1.updatedBy).isEqualTo("actor1");
        assertThat(model1.updatedAt).isEqualTo(now1);
        assertThat(model1.updatedIp).isEqualTo("ip1");
        assertThat(model1.column).isEqualTo("value1");

        Date now2 = new Date(43);
        ExampleHibernateDao dao2 = new ExampleHibernateDao(
                sessionFactory,
                Clock.fixed(now2.toInstant(), ZoneId.systemDefault()),
                new FixedActorProvider("actor2", "ip2"));

        model1.column = "value2";
        model1 = dao2.update(model1);

        assertThat(model1.id).isEqualTo(1);
        assertThat(model1.createdBy).isEqualTo("actor1");
        assertThat(model1.createdAt).isEqualTo(now1);
        assertThat(model1.createdIp).isEqualTo("ip1");
        assertThat(model1.updatedBy).isEqualTo("actor2");
        assertThat(model1.updatedAt).isEqualTo(now2);
        assertThat(model1.updatedIp).isEqualTo("ip2");
        assertThat(model1.column).isEqualTo("value2");

        maybeModel = dao2.select(1);
        assertThat(maybeModel).isPresent();
        model1 = maybeModel.get();

        assertThat(model1.id).isEqualTo(1);
        assertThat(model1.createdBy).isEqualTo("actor1");
        assertThat(model1.createdAt).isEqualTo(now1);
        assertThat(model1.createdIp).isEqualTo("ip1");
        assertThat(model1.updatedBy).isEqualTo("actor2");
        assertThat(model1.updatedAt).isEqualTo(now2);
        assertThat(model1.updatedIp).isEqualTo("ip2");
        assertThat(model1.column).isEqualTo("value2");

        ExampleModel model2 = new ExampleModel();
        model2.column = "value3";
        dao1.insert(model2);

        assertThat(dao1.select(1)).isPresent();
        assertThat(dao1.select(2)).isPresent();
        assertThat(dao1.select(3)).isEmpty();

        dao1.delete(model2);
        assertThat(dao1.select(2)).isEmpty();
    }

    @Nested class selectAll {
        @Test void succeeds(SessionFactory sessionFactory) {
            ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory,
                    new FixedClock(),
                    new FixedActorProvider());

            Page<ExampleModel> page1 = dao.selectAll(1, 10);
            assertThat(page1.getCurrentPage()).isEqualTo(1);
            assertThat(page1.getPageSize()).isEqualTo(10);
            assertThat(page1.getTotalPages()).isZero();
            assertThat(page1.getTotalData()).isZero();
            assertThat(page1.getData()).isEmpty();

            ExampleModel model1 = new ExampleModel();
            model1.column = "value1";
            model1.uniqueColumn = "unique1";
            dao.insert(model1);

            ExampleModel model2 = new ExampleModel();
            model2.column = "value2";
            model2.uniqueColumn = "unique2";
            dao.insert(model2);

            Page<ExampleModel> page2 = dao.selectAll(1, 1);
            assertThat(page2.getCurrentPage()).isEqualTo(1);
            assertThat(page2.getPageSize()).isEqualTo(1);
            assertThat(page2.getTotalPages()).isEqualTo(2);
            assertThat(page2.getTotalData()).isEqualTo(2);
            assertThat(page2.getData()).containsExactly(model1);

            Page<ExampleModel> page3 = dao.selectAll(2, 1);
            assertThat(page3.getCurrentPage()).isEqualTo(2);
            assertThat(page3.getPageSize()).isEqualTo(1);
            assertThat(page3.getTotalPages()).isEqualTo(2);
            assertThat(page3.getTotalData()).isEqualTo(2);
            assertThat(page3.getData()).containsExactly(model2);

            Page<ExampleModel> page4 = dao.selectAll(1, 2);
            assertThat(page4.getCurrentPage()).isEqualTo(1);
            assertThat(page4.getPageSize()).isEqualTo(2);
            assertThat(page4.getTotalPages()).isEqualTo(1);
            assertThat(page4.getTotalData()).isEqualTo(2);
            assertThat(page4.getData()).containsExactly(model1, model2);

            Page<ExampleModel> page5 = dao.selectAll(1, 10);
            assertThat(page5.getCurrentPage()).isEqualTo(1);
            assertThat(page5.getPageSize()).isEqualTo(10);
            assertThat(page5.getTotalPages()).isEqualTo(1);
            assertThat(page5.getTotalData()).isEqualTo(2);
            assertThat(page5.getData()).containsExactly(model1, model2);
        }
    }

    @Nested class selectByUniqueColumn {
        @Test void succeeds(SessionFactory sessionFactory) {
            ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory,
                    new FixedClock(),
                    new FixedActorProvider());

            ExampleModel model1 = new ExampleModel();
            model1.column = "value1";
            model1.uniqueColumn = "unique1";
            dao.insert(model1);

            ExampleModel model2 = new ExampleModel();
            model2.column = "value2";
            model2.uniqueColumn = "unique2";
            dao.insert(model2);

            assertThat(dao.selectByUniqueColumn("unique1")).contains(model1);
            assertThat(dao.selectByUniqueColumn("unique2")).contains(model2);
            assertThat(dao.selectByUniqueColumn("value1")).isEmpty();
        }
    }


    private static class ExampleHibernateDao extends HibernateDao<ExampleModel> {
        ExampleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }

        Optional<ExampleModel> selectByUniqueColumn(String value) {
            return selectByUniqueColumn(ExampleModel_.uniqueColumn, value);
        }
    }
}
