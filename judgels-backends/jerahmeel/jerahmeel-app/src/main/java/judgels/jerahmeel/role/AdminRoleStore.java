package judgels.jerahmeel.role;

import com.google.common.collect.Lists;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.admin.Admin;
import judgels.jerahmeel.persistence.AdminRoleDao;
import judgels.jerahmeel.persistence.AdminRoleModel;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public class AdminRoleStore {
    private final AdminRoleDao adminRoleDao;

    @Inject
    public AdminRoleStore(AdminRoleDao adminRoleDao) {
        this.adminRoleDao = adminRoleDao;
    }

    public Page<Admin> getAdmins(Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder().from(SelectionOptions.DEFAULT_PAGED);
        page.ifPresent(options::page);
        return adminRoleDao.selectPaged(options.build()).mapPage(
                p -> Lists.transform(p, AdminRoleStore::fromModel));
    }

    public boolean upsertAdmin(String userJid) {
        if (adminRoleDao.selectByUserJid(userJid).isPresent()) {
            return false;
        }

        AdminRoleModel model = new AdminRoleModel();
        model.userJid = userJid;
        adminRoleDao.insert(model);
        adminRoleDao.invalidateCache(userJid);
        return true;
    }

    public boolean deleteAdmin(String userJid) {
        Optional<AdminRoleModel> maybeModel = adminRoleDao.selectByUserJid(userJid);
        if (!maybeModel.isPresent()) {
            return false;
        }

        adminRoleDao.delete(maybeModel.get());
        adminRoleDao.invalidateCache(userJid);
        return true;
    }

    private static Admin fromModel(AdminRoleModel model) {
        return new Admin.Builder()
                .userJid(model.userJid)
                .build();
    }
}
