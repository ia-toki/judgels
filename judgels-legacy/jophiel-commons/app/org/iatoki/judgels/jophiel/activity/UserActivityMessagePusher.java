package org.iatoki.judgels.jophiel.activity;

import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.jophiel.JophielClientAPI;
import org.iatoki.judgels.api.jophiel.JophielUserActivityMessage;
import play.db.jpa.JPAApi;

import java.util.List;
import java.util.stream.Collectors;

public final class UserActivityMessagePusher implements Runnable {

    private final JPAApi jpaApi;
    private final JophielClientAPI jophielClientAPI;
    private final UserActivityMessageService userActivityMessageService;

    public UserActivityMessagePusher(JPAApi jpaApi, JophielClientAPI jophielClientAPI, UserActivityMessageService userActivityMessageService) {
        this.jpaApi = jpaApi;
        this.jophielClientAPI = jophielClientAPI;
        this.userActivityMessageService = userActivityMessageService;
    }

    @Override
    public void run() {
        jpaApi.withTransaction(() -> {
                try {
                    List<UserActivityMessage> messages = userActivityMessageService.getUserActivityMessages();
                    List<JophielUserActivityMessage> jophielMessages = messages.stream()
                            .map(m -> new JophielUserActivityMessage(m.getTime(), m.getUserJid(), m.getLog(), m.getIpAddress()))
                            .collect(Collectors.toList());

                    try {
                        jophielClientAPI.sendUserActivityMessages(jophielMessages);
                    } catch (JudgelsAPIClientException e) {
                        userActivityMessageService.addUserActivityMessages(messages);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        );
    }
}
