package judgels.uriel.hibernate;

import static judgels.uriel.UrielCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.AdminRoleModel_;

@Singleton
public class AdminRoleHibernateDao extends UnmodifiableHibernateDao<AdminRoleModel> implements AdminRoleDao {
    private final LoadingCache<String, Boolean> adminCache;

    @Inject
    public AdminRoleHibernateDao(HibernateDaoData data) {
        super(data);

        this.adminCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(getShortDuration())
                .build(this::isAdminUncached);
    }

    @Override
    public Optional<AdminRoleModel> selectByUserJid(String userJid) {
        return selectByUniqueColumn(AdminRoleModel_.userJid, userJid);
    }

    @Override
    public boolean isAdmin(String userJid) {
        return adminCache.get(userJid);
    }

    @Override
    public void invalidateCache(String userJid) {
        adminCache.invalidate(userJid);
    }

    private boolean isAdminUncached(String userJid) {
        return selectByUserJid(userJid).isPresent();
    }
}
