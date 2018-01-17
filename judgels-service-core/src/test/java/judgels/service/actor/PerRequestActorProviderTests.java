package judgels.service.actor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class PerRequestActorProviderTests {
    @Test void sets_and_gets_jid() {
        new Thread(() -> {
            assertThat(PerRequestActorProvider.getJid()).isEmpty();
            PerRequestActorProvider.setJid("jid");
            assertThat(PerRequestActorProvider.getJid()).contains("jid");
        }).start();
    }

    @Test void sets_and_gets_ip_address() {
        new Thread(() -> {
            assertThat(PerRequestActorProvider.getIpAddress()).isEmpty();
            PerRequestActorProvider.setIpAddress("ip");
            assertThat(PerRequestActorProvider.getIpAddress()).contains("ip");
        }).start();
    }
}
