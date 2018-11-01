package judgels.uriel.persistence;

import judgels.persistence.UnmodifiableDao;

public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {
    boolean isAdmin(String userJid);
    void invalidateCache(String userJid);
}
