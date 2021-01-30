package org.iatoki.judgels.sandalphon.problem.bundle.item.statement;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.statement.html.statementItemView;
import play.twirl.api.Html;

public final class StatementItemAdapter implements BundleItemAdapter {
    private final StatementItemConfigAdapter confAdapter;

    public StatementItemAdapter(ObjectMapper mapper) {
        this.confAdapter = new StatementItemConfigAdapter(mapper);
    }

    @Override
    public Html renderViewHtml(BundleItem item, String conf) {
        return statementItemView.render(item, confAdapter.parseConfString(conf));
    }
}
