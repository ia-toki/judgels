package judgels.hibernate;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.UserInfoDao;
import judgels.persistence.UserInfoModel;
import judgels.persistence.UserInfoModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

public class UserInfoHibernateDao extends HibernateDao<UserInfoModel> implements UserInfoDao {
    @Inject
    public UserInfoHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<UserInfoModel> selectByUserJid(String userJid) {
        return select().where(columnEq(UserInfoModel_.userJid, userJid)).unique();
    }

    @Override
    public List<UserInfoModel> selectAllByUserJids(Collection<String> userJids) {
        return select().where(columnIn(UserInfoModel_.userJid, userJids)).all();
    }
}
