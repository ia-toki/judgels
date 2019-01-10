package org.iatoki.judgels.jophiel.activity;

public abstract class AbstractBaseActivityLogServiceImpl<M extends AbstractActivityLogModel> implements BaseActivityLogService {

    private final BaseActivityLogDao<M> activityLogDao;

    public AbstractBaseActivityLogServiceImpl(BaseActivityLogDao<M> activityLogDao) {
        this.activityLogDao = activityLogDao;
    }

    @Override
    public void addActivityLog(ActivityKey activityKey, String username, long time, String userJid, String userIpAddress) {
        M activityLogModel = activityLogDao.createActivityLogModel();

        activityLogModel.username = username;
        activityLogModel.keyAction = activityKey.getKeyAction();
        activityLogModel.parameters = activityKey.toJsonString();
        activityLogModel.time = time;

        activityLogDao.persist(activityLogModel, userJid, userIpAddress);
    }
}
