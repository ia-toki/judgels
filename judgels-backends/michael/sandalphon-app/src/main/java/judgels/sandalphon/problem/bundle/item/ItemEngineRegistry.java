package judgels.sandalphon.problem.bundle.item;

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import judgels.sandalphon.api.problem.bundle.ItemType;

public class ItemEngineRegistry {
    private static final Map<ItemType, ItemEngine> REGISTRY = new LinkedHashMap<>();

    static {
        REGISTRY.put(ItemType.STATEMENT, new StatementItemEngine());
        REGISTRY.put(ItemType.MULTIPLE_CHOICE, new MultipleChoiceItemEngine());
        REGISTRY.put(ItemType.SHORT_ANSWER, new ShortAnswerItemEngine());
        REGISTRY.put(ItemType.ESSAY, new EssayItemEngine());
    }

    private ItemEngineRegistry() {}

    public static ItemEngine getByType(ItemType type) {
        if (!REGISTRY.containsKey(type)) {
            throw new IllegalArgumentException();
        }
        return REGISTRY.get(type);
    }

    public static Map<String, String> getTypes() {
        Map<String, String> types = new LinkedHashMap<>();
        for (Map.Entry<ItemType, ItemEngine> entry : REGISTRY.entrySet()) {
            types.put(entry.getKey().name(), entry.getValue().getName());
        }
        return ImmutableMap.copyOf(types);
    }
}
