package judgels.michael.problem.bundle.item.config;

import java.util.LinkedHashMap;
import java.util.Map;
import judgels.sandalphon.api.problem.bundle.ItemType;

public class ItemConfigAdapterRegistry {
    private static final Map<ItemType, ItemConfigAdapter> REGISTRY = new LinkedHashMap<>();

    static {
        REGISTRY.put(ItemType.STATEMENT, new StatementItemConfigAdapter());
        REGISTRY.put(ItemType.MULTIPLE_CHOICE, new MultipleChoiceItemConfigAdapter());
        REGISTRY.put(ItemType.SHORT_ANSWER, new ShortAnswerItemConfigAdapter());
        REGISTRY.put(ItemType.ESSAY, new EssayItemConfigAdapter());
    }

    private ItemConfigAdapterRegistry() {}

    public static ItemConfigAdapter getByType(ItemType type) {
        if (!REGISTRY.containsKey(type)) {
            throw new IllegalArgumentException();
        }
        return REGISTRY.get(type);
    }
}
