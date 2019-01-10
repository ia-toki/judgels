package org.iatoki.judgels.jophiel.activity;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class UserActivityMessageServiceImpl implements UserActivityMessageService {

    private static UserActivityMessageServiceImpl INSTANCE;
    private final List<UserActivityMessage> userActivities;
    private final Lock lock;

    private UserActivityMessageServiceImpl() {
        this.userActivities = Lists.newArrayList();
        this.lock = new ReentrantLock();
    }

    @Override
    public void addUserActivityMessage(UserActivityMessage userActivityMessage) throws InterruptedException {
        if (!lock.tryLock(10, TimeUnit.SECONDS)) {
            throw new InterruptedException();
        }

        userActivities.add(userActivityMessage);
        lock.unlock();
    }

    @Override
    public void addUserActivityMessages(List<UserActivityMessage> userActivityMessages) throws InterruptedException {
        if (!lock.tryLock(10, TimeUnit.SECONDS)) {
            throw new InterruptedException();
        }

        this.userActivities.addAll(userActivityMessages);
        lock.unlock();
    }

    @Override
    public List<UserActivityMessage> getUserActivityMessages() throws InterruptedException {
        if (!lock.tryLock(10, TimeUnit.SECONDS)) {
            throw new InterruptedException();
        }

        List<UserActivityMessage> activityLogs = Lists.newArrayList(this.userActivities);
        this.userActivities.clear();
        lock.unlock();

        return activityLogs;
    }

    public static synchronized void buildInstance() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("DefaultUserActivityMessageServiceImpl instance has already been built");
        }
        INSTANCE = new UserActivityMessageServiceImpl();
    }

    public static UserActivityMessageServiceImpl getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("DefaultUserActivityMessageServiceImpl instance has not been built");
        }
        return INSTANCE;
    }

}
