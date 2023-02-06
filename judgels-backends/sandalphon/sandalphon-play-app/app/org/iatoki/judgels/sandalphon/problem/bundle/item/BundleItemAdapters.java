package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.ItemType;
import org.iatoki.judgels.sandalphon.problem.bundle.item.essay.EssayItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.multiplechoice.MultipleChoiceItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.shortanswer.ShortAnswerItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.statement.StatementItemAdapter;

public final class BundleItemAdapters {
    private BundleItemAdapters() {}

    public static BundleItemAdapter fromItemType(ItemType itemType, ObjectMapper mapper) {
        switch (itemType) {
            case STATEMENT:
                return new StatementItemAdapter(mapper);
            case MULTIPLE_CHOICE:
                return new MultipleChoiceItemAdapter(mapper);
            case SHORT_ANSWER:
                return new ShortAnswerItemAdapter(mapper);
            case ESSAY:
                return new EssayItemAdapter(mapper);
        }

        return null;
    }
}
