package org.iatoki.judgels.play.model;

import judgels.persistence.ActorProvider;

import java.util.Optional;

public class LegacyActorProvider implements ActorProvider {
    @Override
    public Optional<String> getJid() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getIpAddress() {
        return Optional.empty();
    }
}
