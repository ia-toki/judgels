package org.iatoki.judgels.play.api;

public interface JudgelsAppClientService {

    boolean clientExistsByJid(String clientJid);

    JudgelsAppClient findClientByJid(String clientJid);
}
