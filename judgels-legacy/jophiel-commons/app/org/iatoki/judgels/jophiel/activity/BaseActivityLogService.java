package org.iatoki.judgels.jophiel.activity;

public interface BaseActivityLogService {

    void addActivityLog(ActivityKey activityKey, String username, long time, String userJid, String userIpAddress);
}
