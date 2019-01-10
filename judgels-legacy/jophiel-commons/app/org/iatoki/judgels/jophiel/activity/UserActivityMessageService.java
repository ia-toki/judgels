package org.iatoki.judgels.jophiel.activity;

import java.util.List;

public interface UserActivityMessageService {

    void addUserActivityMessage(UserActivityMessage userActivityMessage) throws InterruptedException;

    void addUserActivityMessages(List<UserActivityMessage> userActivityMessages) throws InterruptedException;

    List<UserActivityMessage> getUserActivityMessages() throws InterruptedException;
}
