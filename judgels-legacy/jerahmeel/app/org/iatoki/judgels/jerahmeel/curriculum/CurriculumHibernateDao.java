package org.iatoki.judgels.jerahmeel.curriculum;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
public final class CurriculumHibernateDao extends JudgelsHibernateDao<CurriculumModel> implements CurriculumDao {

    @Inject
    public CurriculumHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }
}
