package judgels.uriel.persistence;

import judgels.persistence.UnmodifiableDao;

public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {
    boolean existsByUserJid(String userJid);
}
