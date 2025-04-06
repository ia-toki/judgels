package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.NoClass;
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
            visible = true,
            defaultImpl = NoClass.class
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = StatementItemConfig.class),
            @JsonSubTypes.Type(value = MultipleChoiceItemConfig.class),
            @JsonSubTypes.Type(value = ShortAnswerItemConfig.class),
            @JsonSubTypes.Type(value = EssayItemConfig.class)
    })
    ItemConfig getConfig();

    class Builder extends ImmutableItem.Builder {}
}
