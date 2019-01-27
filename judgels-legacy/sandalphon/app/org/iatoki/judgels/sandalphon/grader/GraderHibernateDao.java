package org.iatoki.judgels.sandalphon.grader;

import com.google.common.collect.ImmutableList;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.metamodel.SingularAttribute;
import java.time.Clock;
import java.util.List;

@Singleton
public final class GraderHibernateDao extends JudgelsHibernateDao<GraderModel> implements GraderDao {

    @Inject
    public GraderHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    protected List<SingularAttribute<GraderModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(GraderModel_.name);
    }
}
