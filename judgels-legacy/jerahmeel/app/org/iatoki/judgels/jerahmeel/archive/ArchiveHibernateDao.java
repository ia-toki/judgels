package org.iatoki.judgels.jerahmeel.archive;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class ArchiveHibernateDao extends JudgelsHibernateDao<ArchiveModel> implements ArchiveDao {

    @Inject
    public ArchiveHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
