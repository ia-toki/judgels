package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableWebSecurityConfiguration.class)
public interface WebSecurityConfiguration {
    @JsonProperty("cors")
    CorsConfiguration getCorsConfig();

    class Builder extends ImmutableWebSecurityConfiguration.Builder {
        public Builder() {
            corsConfig(new CorsConfiguration.Builder().build());
        }
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableCorsConfiguration.class)
    interface CorsConfiguration {
        @Value.Default
        default String getAllowedOrigins() {
            return "*";
        }

        @Value.Default
        default boolean getAllowCredentials() {
            return false;
        }

        class Builder extends ImmutableCorsConfiguration.Builder {}
    }
}
