package judgels.uriel.hibernate;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.FilterOptions;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestLogModel;
import judgels.uriel.persistence.ContestLogModel_;

@Singleton
public class ContestLogHibernateDao extends UnmodifiableHibernateDao<ContestLogModel> implements ContestLogDao {
    @Inject
    public ContestLogHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Page<ContestLogModel> selectPaged(
            String contestJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            SelectionOptions options) {

        FilterOptions.Builder<ContestLogModel> filterOptions = new FilterOptions.Builder<ContestLogModel>()
                .putColumnsEq(ContestLogModel_.contestJid, contestJid);
        userJid.ifPresent(jid -> filterOptions.putColumnsEq(UnmodifiableModel_.createdBy, jid));
        problemJid.ifPresent(jid -> filterOptions.putColumnsEq(ContestLogModel_.problemJid, jid));

        return selectPaged(filterOptions.build(), options);
    }
}
