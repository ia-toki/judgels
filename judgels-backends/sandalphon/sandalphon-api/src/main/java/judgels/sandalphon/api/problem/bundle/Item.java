package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.function.Function;
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
            @JsonSubTypes.Type(value = ImmutableStatementItemConfig.class, name = ItemType.Statement),
            @JsonSubTypes.Type(value = ImmutableMultipleChoiceItemConfig.class, name = ItemType.MultipleChoice)
    })
    ItemConfig getConfig();

    default Item processDisplayText(Function<String, String> processor) {
        return new Item.Builder()
                .from(this)
                .config(getConfig().processDisplayText(processor))
                .build();
    }

    class Builder extends ImmutableItem.Builder {}
}
