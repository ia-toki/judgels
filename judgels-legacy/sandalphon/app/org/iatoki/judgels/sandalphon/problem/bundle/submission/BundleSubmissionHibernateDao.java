package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import judgels.persistence.ActorProvider;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Clock;

@Singleton
@Named("bundleSubmissionDao")
public final class BundleSubmissionHibernateDao extends AbstractBundleSubmissionHibernateDao<BundleSubmissionModel> implements BundleSubmissionDao {

    @Inject
    public BundleSubmissionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public BundleSubmissionModel createSubmissionModel() {
        return new BundleSubmissionModel();
    }
}
