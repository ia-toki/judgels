package judgels.jophiel.legacy.session;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

@Singleton
public class LegacySessionHibernateDao extends UnmodifiableHibernateDao<LegacySessionModel>
        implements LegacySessionDao {

    @Inject
    public LegacySessionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<LegacySessionModel> getByAuthCode(String authCode) {
        return selectByUniqueColumn(LegacySessionModel_.authCode, authCode);
    }
}
