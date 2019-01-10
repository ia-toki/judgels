package org.iatoki.judgels.play.sponsor;

import org.iatoki.judgels.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public final class SponsorConfig {

    private final Map<String, String> sponsors;

    @Inject
    public SponsorConfig(@SponsorConfigSource Config config) {
        this.sponsors = config.requireMap("sponsors", String.class);
    }

    public Map<String, String> getSponsors() {
        return sponsors;
    }
}
