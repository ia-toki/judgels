package org.iatoki.judgels.sandalphon;

import org.iatoki.judgels.jophiel.activity.ActivityKey;
import org.iatoki.judgels.jophiel.activity.UserActivityMessage;
import org.iatoki.judgels.jophiel.activity.UserActivityMessageServiceImpl;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.sandalphon.activity.ActivityLogServiceImpl;

public final class SandalphonControllerUtils {

    private static SandalphonControllerUtils INSTANCE;

    public boolean isAdmin() {
        return SandalphonUtils.hasRole("admin");
    }

    public void addActivityLog(ActivityKey activityKey) {
        long time = System.currentTimeMillis();
        ActivityLogServiceImpl.getInstance().addActivityLog(activityKey, SandalphonUtils.getRealUsername(), time, SandalphonUtils.getRealUserJid(), IdentityUtils.getIpAddress());
        String log = SandalphonUtils.getRealUsername() + " " + activityKey.toString();
        try {
            UserActivityMessageServiceImpl.getInstance().addUserActivityMessage(new UserActivityMessage(System.currentTimeMillis(), SandalphonUtils.getRealUserJid(), log, IdentityUtils.getIpAddress()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void buildInstance() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("SandalphonControllerUtils instance has already been built");
        }
        INSTANCE = new SandalphonControllerUtils();
    }

    public static SandalphonControllerUtils getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("SandalphonControllerUtils instance has not been built");
        }
        return INSTANCE;
    }
}
