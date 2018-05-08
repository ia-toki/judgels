package judgels.sandalphon.hibernate;

import java.time.Clock;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.AbstractSubmissionModel;
import judgels.sandalphon.persistence.AbstractSubmissionModel_;
import judgels.sandalphon.persistence.BaseSubmissionDao;
import org.hibernate.SessionFactory;

public abstract class AbstractSubmissionHibernateDao<M extends AbstractSubmissionModel> extends JudgelsHibernateDao<M>
        implements BaseSubmissionDao<M> {

    public AbstractSubmissionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public final Page<M> selectPaged(String containerJid, String userJid, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<M>()
                .putColumnsEq(AbstractSubmissionModel_.containerJid, containerJid)
                .putColumnsEq(JudgelsModel_.createdBy, userJid)
                .build(), options);
    }
}
