package org.iatoki.judgels.api;

public interface JudgelsWithClientAPI<T extends JudgelsClientAPI> {

    T connectToClientAPI(String clientJid, String clientSecret);
}
