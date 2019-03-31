package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItem.class)
public interface Item {
    String getJid();
    ItemType getType();
    Optional<Integer> getNumber();
    String getMeta();

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImmutableStatementItemConfig.class),
            @JsonSubTypes.Type(value = ImmutableMultipleChoiceItemConfig.class),
            @JsonSubTypes.Type(value = ImmutableShortAnswerItemConfig.class),
            @JsonSubTypes.Type(value = ImmutableEssayItemConfig.class)
    })
    ItemConfig getConfig();

    class Builder extends ImmutableItem.Builder {}
}
