package judgels.persistence.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderDir {
    @JsonProperty ASC("asc"),
    @JsonProperty DESC("desc");

    private String value;

    OrderDir(String value) {
        this.value = value;
    }

    @JsonValue
    String toValue() {
        return value;
    }
}
