package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.ItemType;
import org.iatoki.judgels.sandalphon.problem.bundle.item.essay.EssayItemConfigAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.multiplechoice.MultipleChoiceItemConfigAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.shortanswer.ShortAnswerItemConfigAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.statement.StatementItemConfigAdapter;

public final class ItemConfigAdapters {
    private ItemConfigAdapters() {}

    public static ItemConfigAdapter fromItemType(ItemType itemType, ObjectMapper mapper) {
        switch (itemType) {
            case STATEMENT:
                return new StatementItemConfigAdapter(mapper);
            case MULTIPLE_CHOICE:
                return new MultipleChoiceItemConfigAdapter(mapper);
            case SHORT_ANSWER:
                return new ShortAnswerItemConfigAdapter(mapper);
            case ESSAY:
                return new EssayItemConfigAdapter(mapper);
        }

        return null;
    }
}
