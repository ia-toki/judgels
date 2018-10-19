package judgels.persistence.hibernate;

import static judgels.persistence.TestClock.NOW;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.TestActorProvider;
import judgels.persistence.TestClock;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ExampleModel.class})
class HibernateDaoIntegrationTests {
    @Test
    void can_do_basic_crud(SessionFactory sessionFactory) {
        TestClock clock = new TestClock();
        TestActorProvider actorProvider = new TestActorProvider("actor1", "ip1");
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, clock, actorProvider);

        assertThat(dao.select(1)).isEmpty();

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "value1";
        dao.insert(model1);

        Optional<ExampleModel> maybeModel = dao.select(1);
        assertThat(maybeModel).isPresent();
        model1 = maybeModel.get();

        assertThat(model1.id).isEqualTo(1);
        assertThat(model1.createdBy).isEqualTo("actor1");
        assertThat(model1.createdAt).isEqualTo(NOW);
        assertThat(model1.createdIp).isEqualTo("ip1");
        assertThat(model1.updatedBy).isEqualTo("actor1");
        assertThat(model1.updatedAt).isEqualTo(NOW);
        assertThat(model1.updatedIp).isEqualTo("ip1");
        assertThat(model1.column1).isEqualTo("value1");

        clock.tick(Duration.ofSeconds(1));
        actorProvider.setJid("actor2");
        actorProvider.setIpAddress("ip2");

        model1.column1 = "value2";
        model1 = dao.update(model1);

        assertThat(model1.id).isEqualTo(1);
        assertThat(model1.createdBy).isEqualTo("actor1");
        assertThat(model1.createdAt).isEqualTo(NOW);
        assertThat(model1.createdIp).isEqualTo("ip1");
        assertThat(model1.updatedBy).isEqualTo("actor2");
        assertThat(model1.updatedAt).isEqualTo(NOW.plusSeconds(1));
        assertThat(model1.updatedIp).isEqualTo("ip2");
        assertThat(model1.column1).isEqualTo("value2");

        maybeModel = dao.select(1);
        assertThat(maybeModel).isPresent();
        model1 = maybeModel.get();

        assertThat(model1.id).isEqualTo(1);
        assertThat(model1.createdBy).isEqualTo("actor1");
        assertThat(model1.createdAt).isEqualTo(NOW);
        assertThat(model1.createdIp).isEqualTo("ip1");
        assertThat(model1.updatedBy).isEqualTo("actor2");
        assertThat(model1.updatedAt).isEqualTo(NOW.plusSeconds(1));
        assertThat(model1.updatedIp).isEqualTo("ip2");
        assertThat(model1.column1).isEqualTo("value2");

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "value3";
        dao.insert(model2);

        assertThat(dao.select(1)).isPresent();
        assertThat(dao.select(2)).isPresent();
        assertThat(dao.select(3)).isEmpty();

        dao.delete(model2);
        assertThat(dao.select(2)).isEmpty();
    }

    @Test
    void can_select_by_unique_columns(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new TestClock(), new TestActorProvider());

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

    @Test
    void can_select_and_count_all(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new TestClock(), new TestActorProvider());

        Page<ExampleModel> page = dao.selectPaged(new SelectionOptions.Builder()
                .page(1)
                .pageSize(10)
                .build());
        assertThat(page.getTotalData()).isZero();
        assertThat(page.getData()).isEmpty();

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "value1";
        dao.insert(model1);

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "value2";
        dao.insert(model2);

        page = dao.selectPaged(new SelectionOptions.Builder()
                .page(1)
                .pageSize(1)
                .build());
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model2);

        page = dao.selectPaged(new SelectionOptions.Builder()
                .page(2)
                .pageSize(1)
                .build());
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model1);

        page = dao.selectPaged(new SelectionOptions.Builder()
                .page(1)
                .pageSize(2)
                .build());
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model2, model1);

        page = dao.selectPaged(new SelectionOptions.Builder()
                .page(1)
                .pageSize(10)
                .build());
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model2, model1);

        page = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .lastId(1)
                .build());
        assertThat(page.getTotalData()).isEqualTo(1);
        assertThat(page.getData()).containsExactly(model2);
    }

    @Test
    void can_select_and_count_all_by_columns(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new TestClock(), new TestActorProvider());

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

        Page<ExampleModel> page = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.column1, "a1")
                .build());
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model2, model1);

        page = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.column2, "b1")
                .build());
        assertThat(page.getTotalData()).isEqualTo(3);
        assertThat(page.getData()).containsExactly(model4, model3, model1);

        page = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.column2, "b2")
                .build());
        assertThat(page.getTotalData()).isEqualTo(1);
        assertThat(page.getData()).containsExactly(model2);

        page = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.column2, "b3")
                .build());
        assertThat(page.getTotalData()).isZero();
        assertThat(page.getData()).isEmpty();

        page = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.column1, "a1")
                .putColumnsEq(ExampleModel_.column2, "b1")
                .build());
        assertThat(page.getTotalData()).isEqualTo(1);
        assertThat(page.getData()).containsExactly(model1);

        page = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.column1, "a2")
                .putColumnsEq(ExampleModel_.column2, "b1")
                .build());
        assertThat(page.getTotalData()).isEqualTo(2);
        assertThat(page.getData()).containsExactly(model4, model3);

        page = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.column1, "a2")
                .putColumnsEq(ExampleModel_.column2, "b2")
                .build());
        assertThat(page.getTotalData()).isZero();
        assertThat(page.getData()).isEmpty();
    }

    @Test
    void can_select_and_count_all_by_column_in(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new TestClock(), new TestActorProvider());

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

        Page<ExampleModel> models = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.uniqueColumn1, "x")
                .putColumnsIn(ExampleModel_.column1, ImmutableSet.of("a", "d"))
                .build());
        assertThat(models.getData()).containsExactly(model4, model1);

        models = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.uniqueColumn1, "x")
                .putColumnsIn(ExampleModel_.column1, ImmutableSet.of())
                .build());
        assertThat(models.getData()).isEmpty();
    }

    @Test
    void can_select_with_custom_predicates(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new TestClock(), new TestActorProvider());

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
        model3.column1 = "a";
        model3.column2 = "b";
        model3.uniqueColumn1 = "y";
        dao.insert(model3);

        Optional<ExampleModel> model = dao.selectByFilter(new FilterOptions.Builder<ExampleModel>()
                .putColumnsEq(ExampleModel_.uniqueColumn1, "x")
                .addCustomPredicates((cb, cq, root) -> cb.equal(root.get(ExampleModel_.column1), "a"))
                .build());
        assertThat(model).contains(model1);

        Page<ExampleModel> models = dao.selectPaged(new FilterOptions.Builder<ExampleModel>()
                .addCustomPredicates((cb, cq, root) -> cb.equal(root.get(ExampleModel_.column1), "a"))
                .build());
        assertThat(models.getTotalData()).isEqualTo(2);
        assertThat(models.getData()).containsExactly(model3, model1);
    }

    @Test
    void can_select_all_with_order(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new TestClock(), new TestActorProvider());

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "b";
        dao.insert(model1);

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "a";
        dao.insert(model2);

        ExampleModel model3 = new ExampleModel();
        model3.column1 = "c";
        dao.insert(model3);

        Page<ExampleModel> models = dao.selectPaged(
                new FilterOptions.Builder<ExampleModel>().build());
        assertThat(models.getData()).containsExactly(model3, model2, model1);

        models = dao.selectPaged(new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .orderBy("column1")
                .orderDir(OrderDir.ASC)
                .build());
        assertThat(models.getData()).containsExactly(model2, model1, model3);

        models = dao.selectPaged(new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .orderBy("column1")
                .orderDir(OrderDir.DESC)
                .build());
        assertThat(models.getData()).containsExactly(model3, model1, model2);
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
