package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItem.class)
public interface Item {
    String getJid();
    ItemType getType();
    String getMeta();

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ImmutableStatementItemConfig.class, name = "STATEMENT"),
            @JsonSubTypes.Type(value = ImmutableMultipleChoiceItemConfig.class, name = "MULTIPLE_CHOICE")
    })
    ItemConfig getConfig();

    class Builder extends ImmutableItem.Builder {}
}
