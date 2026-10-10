package judgels.api.catalog.problem.bundle.item;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.NoClass;
import judgels.api.catalog.problem.bundle.EssayItemConfig;
import judgels.api.catalog.problem.bundle.ItemConfig;
import judgels.api.catalog.problem.bundle.ItemType;
import judgels.api.catalog.problem.bundle.MultipleChoiceItemConfig;
import judgels.api.catalog.problem.bundle.ShortAnswerItemConfig;
import judgels.api.catalog.problem.bundle.StatementItemConfig;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemItemUpdateData.class)
public interface ProblemItemUpdateData {
    // The type of the item being updated, which tells how to read the config.
    ItemType getType();

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

    class Builder extends ImmutableProblemItemUpdateData.Builder {}
}
