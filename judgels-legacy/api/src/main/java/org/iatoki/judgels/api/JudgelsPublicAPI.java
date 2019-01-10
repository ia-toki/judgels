package org.iatoki.judgels.api;

public interface JudgelsPublicAPI {

    void useOnBehalfOfUser(String accessToken);

    void useAnonymously();
}
