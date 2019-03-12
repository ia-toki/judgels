package judgels.sandalphon.problem.bundle;

import javax.inject.Inject;
import judgels.sandalphon.api.problem.bundle.ItemType;

public class ItemProcessorRegistry {
    private static final StatementItemProcessor STATEMENT_ITEM_PROCESSOR = new StatementItemProcessor();
    private static final MultipleChoiceItemProcessor MULTIPLE_CHOICE_ITEM_PROCESSOR = new MultipleChoiceItemProcessor();

    @Inject
    public ItemProcessorRegistry() {}

    public ItemProcessor get(ItemType type) {
        if (type == ItemType.STATEMENT) {
            return STATEMENT_ITEM_PROCESSOR;
        } else if (type == ItemType.MULTIPLE_CHOICE) {
            return MULTIPLE_CHOICE_ITEM_PROCESSOR;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
