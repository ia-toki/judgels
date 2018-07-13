package judgels.jophiel.role;

import javax.inject.Inject;

public class SuperadminRoleStore {
    private static String superadminUserJid;

    @Inject
    public SuperadminRoleStore() {}

    public void setSuperadmin(String userJid) {
        superadminUserJid = userJid;
    }

    public boolean isSuperadmin(String userJid) {
        return userJid.equals(superadminUserJid);
    }
}
