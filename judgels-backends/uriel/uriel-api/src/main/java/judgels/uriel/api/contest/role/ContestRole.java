package judgels.uriel.api.contest.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ContestRole {
    @JsonProperty ADMIN("ADMIN"),
    @JsonProperty MANAGER("MANAGER"),
    @JsonProperty SUPERVISOR("SUPERVISOR"),
    @JsonProperty CONTESTANT("CONTESTANT");


    private final String text;

    ContestRole(String text) {
        this.text = text;
    }

    @JsonValue
    public String toString() {
        return this.text;
    }
}
