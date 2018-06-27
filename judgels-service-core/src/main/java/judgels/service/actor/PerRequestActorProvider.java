package judgels.service.actor;

import java.util.Optional;

public class PerRequestActorProvider {
    private PerRequestActorProvider() {}

    private static ThreadLocal<String> threadLocalJid = new ThreadLocal<>();
    private static ThreadLocal<String> threadLocalIpAddress = new ThreadLocal<>();

    public static void setJid(String jid) {
        threadLocalJid.set(jid);
    }

    public static void clearJid() {
        threadLocalJid.remove();
    }

    public static void setIpAddress(String ipAddress) {
        threadLocalIpAddress.set(ipAddress);
    }

    public static Optional<String> getJid() {
        return Optional.ofNullable(threadLocalJid.get());
    }

    public static Optional<String> getIpAddress() {
        return Optional.ofNullable(threadLocalIpAddress.get());
    }
}
