package judgels.service.api.client;

import java.util.Base64;
import org.immutables.value.Value;

@Value.Immutable
public abstract class BasicAuthHeader {
    private static final String PREFIX = "Basic ";

    protected abstract String getAuthHeader();

    @Value.Auxiliary
    public abstract Client getClient();

    public static BasicAuthHeader valueOf(String authHeader) {
        if (!authHeader.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Auth header must start with '" + PREFIX + "'");
        }

        String credentialsPart = authHeader.substring(PREFIX.length());
        String decodedCredentialsPart;
        try {
            decodedCredentialsPart = new String(Base64.getDecoder().decode(credentialsPart));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Auth header must contain base64-encoded jid:secret");
        }

        String[] decodedCredentials = decodedCredentialsPart.split(":");
        if (decodedCredentials.length != 2) {
            throw new IllegalArgumentException("Auth header must contain base64-encoded jid:secret");
        }

        return ImmutableBasicAuthHeader.builder()
                .authHeader(authHeader)
                .client(Client.of(decodedCredentials[0], decodedCredentials[1]))
                .build();
    }

    public static BasicAuthHeader of(Client client) {
        String credentialsPart = client.getJid() + ":" + client.getSecret();
        String encodedCredentialsPart = Base64.getEncoder().encodeToString(credentialsPart.getBytes());
        String authHeader = PREFIX + encodedCredentialsPart;

        return ImmutableBasicAuthHeader.builder()
                .authHeader(authHeader)
                .client(client)
                .build();
    }

    @Override
    public String toString() {
        return getAuthHeader();
    }
}
