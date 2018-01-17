package judgels.jophiel.role;

import judgels.persistence.UnmodifiableDao;

public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {
    boolean existsByUserJid(String userJid);
}
