package judgels.service.api.actor;

import org.immutables.value.Value;

@Value.Immutable
public abstract class AuthHeader {
    private static final String PREFIX = "Bearer ";

    protected abstract String getAuthHeader();

    @Value.Auxiliary
    public abstract String getBearerToken();

    public static AuthHeader valueOf(String authHeader) {
        if (!authHeader.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Auth header must start with '" + PREFIX + "'");
        }

        String bearerToken = authHeader.substring(PREFIX.length());
        return ImmutableAuthHeader.builder()
                .authHeader(authHeader)
                .bearerToken(bearerToken)
                .build();
    }

    public static AuthHeader of(String bearerToken) {
        String authHeader = PREFIX + bearerToken;
        return ImmutableAuthHeader.builder()
                .authHeader(authHeader)
                .bearerToken(bearerToken)
                .build();
    }

    @Override
    public String toString() {
        return getAuthHeader();
    }
}
