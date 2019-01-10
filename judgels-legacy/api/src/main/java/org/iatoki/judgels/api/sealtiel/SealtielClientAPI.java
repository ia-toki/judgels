package org.iatoki.judgels.api.sealtiel;

import org.iatoki.judgels.api.JudgelsClientAPI;

public interface SealtielClientAPI extends JudgelsClientAPI {

    SealtielMessage fetchMessage();

    void acknowledgeMessage(long messageId);

    void extendMessageTimeout(long messageId);

    void sendMessage(String targetClientJid, String messageType, String message);

    void sendLowPriorityMessage(String targetClientJid, String messageType, String message);
}
