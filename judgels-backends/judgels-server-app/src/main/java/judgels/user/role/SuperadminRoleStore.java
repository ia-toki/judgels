package judgels.user.role;

import jakarta.inject.Inject;
import java.util.Optional;

public class SuperadminRoleStore {
    private static String superadminUserJid;

    @Inject
    public SuperadminRoleStore() {}

    public void setSuperadmin(String userJid) {
        superadminUserJid = userJid;
    }

    public Optional<String> getSuperadminJid() {
        return Optional.ofNullable(superadminUserJid);
    }

    public boolean isSuperadmin(String userJid) {
        return userJid.equals(superadminUserJid);
    }
}
