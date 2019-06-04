package org.iatoki.judgels.jerahmeel;

import org.iatoki.judgels.jerahmeel.activity.ActivityLogServiceImpl;
import org.iatoki.judgels.jophiel.activity.ActivityKey;
import org.iatoki.judgels.jophiel.activity.UserActivityMessage;
import org.iatoki.judgels.jophiel.activity.UserActivityMessageServiceImpl;
import org.iatoki.judgels.play.IdentityUtils;

public final class JerahmeelControllerUtils {

    private static JerahmeelControllerUtils instance;

    public static synchronized void buildInstance() {
        if (instance != null) {
            throw new UnsupportedOperationException("JerahmeelControllerUtils instance has already been built");
        }
        instance = new JerahmeelControllerUtils();
    }

    public static JerahmeelControllerUtils getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("JerahmeelControllerUtils instance has not been built");
        }
        return instance;
    }

    public boolean isAdmin() {
        return JerahmeelUtils.hasRole("admin");
    }

    public void addActivityLog(ActivityKey activityKey) {
        if (!JerahmeelUtils.isGuest()) {
            long time = System.currentTimeMillis();
            ActivityLogServiceImpl.getInstance().addActivityLog(activityKey, JerahmeelUtils.getRealUsername(), time, JerahmeelUtils.getRealUserJid(), IdentityUtils.getIpAddress());
            String log = JerahmeelUtils.getRealUsername() + " " + activityKey.toString();
            try {
                UserActivityMessageServiceImpl.getInstance().addUserActivityMessage(new UserActivityMessage(System.currentTimeMillis(), JerahmeelUtils.getRealUserJid(), log, IdentityUtils.getIpAddress()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
