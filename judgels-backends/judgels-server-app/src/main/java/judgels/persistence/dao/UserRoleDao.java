package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.util.List;
import judgels.api.user.role.UserRole;
import judgels.persistence.DaoData;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.UnmodifiableModel_;
import judgels.persistence.api.OrderDir;
import judgels.persistence.model.UserRoleModel;
import judgels.persistence.model.UserRoleModel_;

public class UserRoleDao extends UnmodifiableDao<UserRoleModel> {
    @Inject
    public UserRoleDao(DaoData data) {
        super(data);
    }

    public List<UserRoleModel> selectAll() {
        return select()
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }

    public List<UserRoleModel> selectAllByUserJid(String userJid) {
        return select()
                .where(columnEq(UserRoleModel_.userJid, userJid))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }

    public List<UserRoleModel> selectAllByRole(UserRole role) {
        return select()
                .where(columnEq(UserRoleModel_.role, role.name()))
                .orderBy(UnmodifiableModel_.ID, OrderDir.ASC)
                .all();
    }
}
