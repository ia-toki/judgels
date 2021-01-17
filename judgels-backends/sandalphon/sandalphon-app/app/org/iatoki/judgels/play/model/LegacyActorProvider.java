package org.iatoki.judgels.play.model;

import java.util.Optional;
import judgels.persistence.ActorProvider;

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
