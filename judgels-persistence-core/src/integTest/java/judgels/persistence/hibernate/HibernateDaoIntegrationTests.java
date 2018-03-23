package judgels.persistence.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import judgels.persistence.ActorProvider;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.Page;
import org.hibernate.SessionFactory;
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
        model1.column1 = "value1";
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
        assertThat(model1.column1).isEqualTo("value1");

        Date now2 = new Date(43);
        ExampleHibernateDao dao2 = new ExampleHibernateDao(
                sessionFactory,
                Clock.fixed(now2.toInstant(), ZoneId.systemDefault()),
                new FixedActorProvider("actor2", "ip2"));

        model1.column1 = "value2";
        model1 = dao2.update(model1);

        assertThat(model1.id).isEqualTo(1);
        assertThat(model1.createdBy).isEqualTo("actor1");
        assertThat(model1.createdAt).isEqualTo(now1);
        assertThat(model1.createdIp).isEqualTo("ip1");
        assertThat(model1.updatedBy).isEqualTo("actor2");
        assertThat(model1.updatedAt).isEqualTo(now2);
        assertThat(model1.updatedIp).isEqualTo("ip2");
        assertThat(model1.column1).isEqualTo("value2");

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
        assertThat(model1.column1).isEqualTo("value2");

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "value3";
        dao1.insert(model2);

        assertThat(dao1.select(1)).isPresent();
        assertThat(dao1.select(2)).isPresent();
        assertThat(dao1.select(3)).isEmpty();

        dao1.delete(model2);
        assertThat(dao1.select(2)).isEmpty();
    }

    @Test void can_select_by_unique_columns(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "value1";
        model1.uniqueColumn = "unique1";
        model1.uniqueColumn1 = "a1";
        model1.uniqueColumn2 = "b1";
        dao.insert(model1);

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "value2";
        model2.uniqueColumn = "unique2";
        model2.uniqueColumn1 = "a1";
        model2.uniqueColumn2 = "b2";
        dao.insert(model2);

        ExampleModel model3 = new ExampleModel();
        model3.column1 = "value2";
        model3.uniqueColumn1 = "a2";
        model3.uniqueColumn2 = "b2";
        dao.insert(model3);

        assertThat(dao.selectByUniqueColumn("unique1")).contains(model1);
        assertThat(dao.selectByUniqueColumn("unique2")).contains(model2);
        assertThat(dao.selectByUniqueColumn("value1")).isEmpty();

        assertThat(dao.selectByUniqueColumns("a1", "b1")).contains(model1);
        assertThat(dao.selectByUniqueColumns("a1", "b2")).contains(model2);
        assertThat(dao.selectByUniqueColumns("a2", "b1")).isEmpty();
    }

    @Test void can_select_and_count_all(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        Page<ExampleModel> page = dao.selectAll(1, 10);
        assertThat(page.getTotalData()).isZero();
        assertThat(page.getData()).isEmpty();

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "value1";
        dao.insert(model1);

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "value2";
        dao.insert(model2);

        page = dao.selectAll(1, 1);
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model1);

        page = dao.selectAll(2, 1);
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model2);

        page = dao.selectAll(1, 2);
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model1, model2);

        page = dao.selectAll(1, 10);
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model1, model2);
    }

    @Test void can_select_and_count_all_by_columns(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "a1";
        model1.column2 = "b1";
        dao.insert(model1);

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "a1";
        model2.column2 = "b2";
        dao.insert(model2);

        ExampleModel model3 = new ExampleModel();
        model3.column1 = "a2";
        model3.column2 = "b1";
        dao.insert(model3);

        ExampleModel model4 = new ExampleModel();
        model4.column1 = "a2";
        model4.column2 = "b1";
        model4.uniqueColumn = "unique";
        dao.insert(model4);

        Page<ExampleModel> page = dao.selectAllByColumn(ExampleModel_.column1, "a1", 1, 10);
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model1, model2);

        page = dao.selectAllByColumn(ExampleModel_.column2, "b1", 1, 10);
        assertThat(page.getTotalData()).isEqualTo(3);
        assertThat(page.getData()).containsExactly(model1, model3, model4);

        page = dao.selectAllByColumn(ExampleModel_.column2, "b2", 1, 10);
        assertThat(page.getTotalData()).isEqualTo(1);
        assertThat(page.getData()).containsExactly(model2);

        page = dao.selectAllByColumn(ExampleModel_.column2, "b3", 1, 10);
        assertThat(page.getTotalData()).isZero();
        assertThat(page.getData()).isEmpty();

        page = dao.selectAllByColumns(
                ImmutableMap.of(ExampleModel_.column1, "a1", ExampleModel_.column2, "b1"), 1, 10);
        assertThat(page.getTotalData()).isEqualTo(1);
        assertThat(page.getData()).containsExactly(model1);

        page = dao.selectAllByColumns(
                ImmutableMap.of(ExampleModel_.column1, "a2", ExampleModel_.column2, "b1"), 1, 10);
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model3, model4);

        page = dao.selectAllByColumns(
                ImmutableMap.of(ExampleModel_.column1, "a2", ExampleModel_.column2, "b2"), 1, 10);
        assertThat(page.getTotalData()).isZero();
        assertThat(page.getData()).isEmpty();
    }

    @Test void can_select_and_count_all_by_column_in(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "a";
        model1.column2 = "b";
        model1.uniqueColumn1 = "x";
        dao.insert(model1);

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "b";
        model2.column2 = "a";
        model2.uniqueColumn1 = "x";
        dao.insert(model2);

        ExampleModel model3 = new ExampleModel();
        model3.column1 = "c";
        model3.column2 = "c";
        model3.uniqueColumn1 = "x";
        dao.insert(model3);

        ExampleModel model4 = new ExampleModel();
        model4.column1 = "a";
        model4.column2 = "c";
        model4.uniqueColumn1 = "x";
        dao.insert(model4);

        ExampleModel model5 = new ExampleModel();
        model5.column1 = "a";
        model5.column2 = "e";
        model5.uniqueColumn1 = "y";
        dao.insert(model5);

        List<ExampleModel> models = dao.selectAllByColumnIn(
                ImmutableMap.of(ExampleModel_.uniqueColumn1, "x"),
                ExampleModel_.column1,
                ImmutableSet.of("a", "d"));
        assertThat(models).containsExactly(model1, model4);
    }

    private static class ExampleHibernateDao extends HibernateDao<ExampleModel> {
        ExampleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(sessionFactory, clock, actorProvider);
        }

        Optional<ExampleModel> selectByUniqueColumn(String value) {
            return selectByUniqueColumn(ExampleModel_.uniqueColumn, value);
        }

        Optional<ExampleModel> selectByUniqueColumns(String value1, String value2) {
            return selectByUniqueColumns(
                    ImmutableMap.of(ExampleModel_.uniqueColumn1, value1, ExampleModel_.uniqueColumn2, value2));
        }
    }
}
