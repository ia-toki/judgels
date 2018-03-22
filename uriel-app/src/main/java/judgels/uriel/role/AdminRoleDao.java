package judgels.uriel.role;

import judgels.persistence.UnmodifiableDao;
import judgels.uriel.persistence.AdminRoleModel;

public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {
    boolean existsByUserJid(String userJid);
}
