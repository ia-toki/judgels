package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableStatementItemConfig.class),
        @JsonSubTypes.Type(value = ImmutableMultipleChoiceItemConfig.class),
        @JsonSubTypes.Type(value = ImmutableShortAnswerItemConfig.class),
        @JsonSubTypes.Type(value = ImmutableEssayItemConfig.class)
})
public interface ItemConfig {
    String getStatement();
}
