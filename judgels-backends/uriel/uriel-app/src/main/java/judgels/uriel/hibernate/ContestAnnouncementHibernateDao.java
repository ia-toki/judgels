package judgels.uriel.hibernate;

import java.time.Clock;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestAnnouncementModel_;
import org.hibernate.SessionFactory;

@Singleton
public class ContestAnnouncementHibernateDao extends JudgelsHibernateDao<ContestAnnouncementModel> implements
        ContestAnnouncementDao {

    @Inject
    public ContestAnnouncementHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Page<ContestAnnouncementModel> selectPagedByContestJid(String contestJid, SelectionOptions options) {
        return selectPaged(new FilterOptions.Builder<ContestAnnouncementModel>()
                .putColumnsEq(ContestAnnouncementModel_.contestJid, contestJid)
                .build(), options);
    }

    @Override
    public Page<ContestAnnouncementModel> selectPagedPublishedByContestJid(
            String contestJid,
            SelectionOptions options) {

        return selectPaged(new FilterOptions.Builder<ContestAnnouncementModel>()
                .putColumnsEq(ContestAnnouncementModel_.contestJid, contestJid)
                .putColumnsEq(ContestAnnouncementModel_.status, ContestAnnouncementStatus.PUBLISHED.name())
                .build(), options);
    }

    @Override
    public long selectCountPublishedByContestJid(String contestJid) {
        return selectCount(new FilterOptions.Builder<ContestAnnouncementModel>()
                .putColumnsEq(ContestAnnouncementModel_.contestJid, contestJid)
                .putColumnsEq(ContestAnnouncementModel_.status, ContestAnnouncementStatus.PUBLISHED.name())
                .build());
    }
}
