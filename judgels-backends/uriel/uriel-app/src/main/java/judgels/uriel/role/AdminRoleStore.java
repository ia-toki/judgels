package judgels.uriel.role;

import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.admin.Admin;
import judgels.uriel.api.dump.AdminRoleDump;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.AdminRoleModel;

public class AdminRoleStore {
    private static final int PAGE_SIZE = 250;

    private final AdminRoleDao adminRoleDao;

    @Inject
    public AdminRoleStore(AdminRoleDao adminRoleDao) {
        this.adminRoleDao = adminRoleDao;
    }

    public Page<Admin> getAdmins(Optional<Integer> page) {
        SelectionOptions.Builder options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .pageSize(PAGE_SIZE);
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

    public void importDump(AdminRoleDump adminRoleDump) {
        AdminRoleModel adminRoleModel = new AdminRoleModel();
        adminRoleModel.userJid = adminRoleDump.getUserJid();
        adminRoleDao.setModelMetadataFromDump(adminRoleModel, adminRoleDump);
        adminRoleDao.persist(adminRoleModel);
        adminRoleDao.invalidateCache(adminRoleModel.userJid);
    }

    public Set<AdminRoleDump> exportDumps() {
        return adminRoleDao.selectAll(SelectionOptions.DEFAULT_ALL).stream()
                .map(adminRoleModel -> new AdminRoleDump.Builder()
                        .mode(DumpImportMode.RESTORE)
                        .userJid(adminRoleModel.userJid)
                        .createdAt(adminRoleModel.createdAt)
                        .createdBy(Optional.ofNullable(adminRoleModel.createdBy))
                        .createdIp(Optional.ofNullable(adminRoleModel.createdIp))
                        .build())
                .collect(Collectors.toSet());
    }
}
