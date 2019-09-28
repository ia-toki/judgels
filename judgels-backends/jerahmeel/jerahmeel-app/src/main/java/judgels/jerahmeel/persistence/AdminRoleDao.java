package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.UnmodifiableDao;

public interface AdminRoleDao extends UnmodifiableDao<AdminRoleModel> {
    Optional<AdminRoleModel> selectByUserJid(String userJid);
    boolean isAdmin(String userJid);
    void invalidateCache(String userJid);
}
