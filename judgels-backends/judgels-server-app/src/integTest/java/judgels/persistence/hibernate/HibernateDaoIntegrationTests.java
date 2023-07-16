package judgels.persistence.hibernate;

import static judgels.persistence.TestClock.NOW;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.ActorProvider;
import judgels.persistence.TestActorProvider;
import judgels.persistence.TestClock;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ExampleModel.class})
class HibernateDaoIntegrationTests {
    @Test
    void crud_flow(SessionFactory sessionFactory) {
        TestClock clock = new TestClock();
        TestActorProvider actorProvider = new TestActorProvider("actor1", "ip1");
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, clock, actorProvider);

        assertThat(dao.selectById(1)).isEmpty();

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "value1";
        dao.insert(model1);

        Optional<ExampleModel> maybeModel = dao.selectById(1);
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

        maybeModel = dao.selectById(1);
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

        assertThat(dao.selectById(1)).isPresent();
        assertThat(dao.selectById(2)).isPresent();
        assertThat(dao.selectById(3)).isEmpty();

        dao.delete(model2);
        assertThat(dao.selectById(2)).isEmpty();
    }

    @Test
    void select_unique(SessionFactory sessionFactory) {
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
    void select_paged(SessionFactory sessionFactory) {
        ExampleHibernateDao dao = new ExampleHibernateDao(sessionFactory, new TestClock(), new TestActorProvider());

        Page<ExampleModel> data = dao.select().paged(1, 10);
        assertThat(data.getTotalCount()).isZero();
        assertThat(data.getPageNumber()).isEqualTo(1);
        assertThat(data.getPageSize()).isEqualTo(10);
        assertThat(data.getPage()).isEmpty();

        ExampleModel model1 = new ExampleModel();
        model1.column1 = "value1";
        dao.insert(model1);

        ExampleModel model2 = new ExampleModel();
        model2.column1 = "value2";
        dao.insert(model2);

        data = dao.select().paged(1, 1);
        assertThat(data.getTotalCount()).isEqualTo(2);
        assertThat(data.getPageNumber()).isEqualTo(1);
        assertThat(data.getPageSize()).isEqualTo(1);
        assertThat(data.getPage()).containsExactly(model2);

        data = dao.select().paged(2, 1);
        assertThat(data.getTotalCount()).isEqualTo(2);
        assertThat(data.getPageNumber()).isEqualTo(2);
        assertThat(data.getPageSize()).isEqualTo(1);
        assertThat(data.getPage()).containsExactly(model1);

        data = dao.select().paged(1, 2);
        assertThat(data.getTotalCount()).isEqualTo(2);
        assertThat(data.getPageNumber()).isEqualTo(1);
        assertThat(data.getPageSize()).isEqualTo(2);
        assertThat(data.getPage()).containsExactly(model2, model1);

        data = dao.select().paged(1, 10);
        assertThat(data.getTotalCount()).isEqualTo(2);
        assertThat(data.getPageNumber()).isEqualTo(1);
        assertThat(data.getPageSize()).isEqualTo(10);
        assertThat(data.getPage()).containsExactly(model2, model1);
    }

    @Test
    void select_paged_by_columns(SessionFactory sessionFactory) {
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

        Page<ExampleModel> data = dao
                .select()
                .whereColumn1Is("a1")
                .paged(1, 20);
        assertThat(data.getTotalCount()).isEqualTo(2);
        assertThat(data.getPage()).containsExactly(model2, model1);

        data = dao
                .select()
                .whereColumn2Is("b1")
                .paged(1, 20);
        assertThat(data.getTotalCount()).isEqualTo(3);
        assertThat(data.getPage()).containsExactly(model4, model3, model1);

        data = dao
                .select()
                .whereColumn2Is("b2")
                .paged(1, 20);
        assertThat(data.getTotalCount()).isEqualTo(1);
        assertThat(data.getPage()).containsExactly(model2);

        data = dao
                .select()
                .whereColumn2Is("b3")
                .paged(1, 20);
        assertThat(data.getTotalCount()).isZero();
        assertThat(data.getPage()).isEmpty();

        data = dao
                .select()
                .whereColumn1Is("a1")
                .whereColumn2Is("b1")
                .paged(1, 20);
        assertThat(data.getTotalCount()).isEqualTo(1);
        assertThat(data.getPage()).containsExactly(model1);

        data = dao
                .select()
                .whereColumn1Is("a2")
                .whereColumn2Is("b1")
                .paged(1, 20);
        assertThat(data.getTotalCount()).isEqualTo(2);
        assertThat(data.getPage()).containsExactly(model4, model3);

        data = dao
                .select()
                .whereColumn1Is("a2")
                .whereColumn2Is("b2")
                .paged(1, 20);
        assertThat(data.getTotalCount()).isZero();
        assertThat(data.getPage()).isEmpty();
    }

    @Test
    void select_paged_by_column_in(SessionFactory sessionFactory) {
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

        Page<ExampleModel> models = dao.select()
                .whereUniqueColumn1Is("x")
                .whereColumn1In(Set.of("a", "d"))
                .paged(1, 20);
        assertThat(models.getPage()).containsExactly(model4, model1);

        models = dao.select()
                .whereUniqueColumn1Is("x")
                .whereColumn1In(Set.of())
                .paged(1, 20);
        assertThat(models.getPage()).isEmpty();
    }

    @Test
    void select_with_custom_predicates(SessionFactory sessionFactory) {
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

        Optional<ExampleModel> model = dao
                .select()
                .whereUniqueColumn1Is("x")
                .where((cb, cq, root) -> cb.equal(root.get(ExampleModel_.column1), "a"))
                .unique();
        assertThat(model).contains(model1);

        Page<ExampleModel> models = dao
                .select()
                .where((cb, cq, root) -> cb.equal(root.get(ExampleModel_.column1), "a"))
                .paged(1, 20);
        assertThat(models.getTotalCount()).isEqualTo(2);
        assertThat(models.getPage()).containsExactly(model3, model1);
    }

    @Test
    void select_with_order(SessionFactory sessionFactory) {
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

        List<ExampleModel> models = dao.select().all();
        assertThat(models).containsExactly(model3, model2, model1);

        models = dao
                .select()
                .orderBy(ExampleModel_.COLUMN1, OrderDir.ASC)
                .all();
        assertThat(models).containsExactly(model2, model1, model3);

        models = dao
                .select()
                .orderBy(ExampleModel_.COLUMN1, OrderDir.DESC)
                .all();
        assertThat(models).containsExactly(model3, model1, model2);
    }

    private static class ExampleHibernateDao extends HibernateDao<ExampleModel> {
        ExampleHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
            super(new HibernateDaoData(sessionFactory, clock, actorProvider));
        }

        public ExampleHibernateQueryBuilder select() {
            return new ExampleHibernateQueryBuilder(currentSession());
        }

        Optional<ExampleModel> selectByUniqueColumn(String value) {
            return select().where(columnEq(ExampleModel_.uniqueColumn, value)).unique();
        }

        Optional<ExampleModel> selectByUniqueColumns(String value1, String value2) {
            return select()
                    .where(columnEq(ExampleModel_.uniqueColumn1, value1))
                    .where(columnEq(ExampleModel_.uniqueColumn2, value2))
                    .unique();
        }

        private static class ExampleHibernateQueryBuilder extends HibernateQueryBuilder<ExampleModel> {
            ExampleHibernateQueryBuilder(Session currentSession) {
                super(currentSession, ExampleModel.class);
            }

            public ExampleHibernateQueryBuilder whereUniqueColumn1Is(String value) {
                where(columnEq(ExampleModel_.uniqueColumn1, value));
                return this;
            }

            public ExampleHibernateQueryBuilder whereColumn1Is(String value) {
                where(columnEq(ExampleModel_.column1, value));
                return this;
            }

            public ExampleHibernateQueryBuilder whereColumn1In(Collection<String> values) {
                where(columnIn(ExampleModel_.column1, values));
                return this;
            }

            public ExampleHibernateQueryBuilder whereColumn2Is(String value) {
                where(columnEq(ExampleModel_.column2, value));
                return this;
            }
        }
    }
}
