package judgels.jophiel.api.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    @JsonProperty ADMIN("admin"),
    @JsonProperty SUPERADMIN("superadmin"),
    @JsonProperty USER("user");

    private final String text;

    Role(String text) {
        this.text = text;
    }

    @JsonValue
    public String toString() {
        return this.text;
    }
}
