package org.iatoki.judgels.api;

public interface JudgelsWithPublicAPI<T extends JudgelsPublicAPI> {

    T connectToPublicAPI();
}
