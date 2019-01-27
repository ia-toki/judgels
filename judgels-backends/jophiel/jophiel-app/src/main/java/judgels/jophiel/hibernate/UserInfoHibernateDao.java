package judgels.jophiel.hibernate;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.UserInfoDao;
import judgels.jophiel.persistence.UserInfoModel;
import judgels.jophiel.persistence.UserInfoModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

@Singleton
public class UserInfoHibernateDao extends HibernateDao<UserInfoModel> implements UserInfoDao {
    @Inject
    public UserInfoHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<UserInfoModel> selectByUserJid(String userJid) {
        return selectByUniqueColumn(UserInfoModel_.userJid, userJid);
    }

    @Override
    public Map<String, UserInfoModel> selectAllByUserJids(Set<String> userJids) {
        return selectAll(new FilterOptions.Builder<UserInfoModel>()
                .putColumnsIn(UserInfoModel_.userJid, userJids)
                .build())
                .stream()
                .collect(Collectors.toMap(m -> m.userJid, m -> m));
    }
}
