package org.iatoki.judgels.sandalphon.problem.bundle.item.essay;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemHasScore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.essay.html.essayItemView;
import play.twirl.api.Html;

public final class EssayItemAdapter implements BundleItemAdapter, BundleItemHasScore {
    private final EssayItemConfigAdapter confAdapter;

    public EssayItemAdapter(ObjectMapper mapper) {
        this.confAdapter = new EssayItemConfigAdapter(mapper);
    }

    @Override
    public Html renderViewHtml(BundleItem item, String conf) {
        return essayItemView.render(item, confAdapter.parseConfString(conf));
    }

    @Override
    public double calculateScore(String conf, String answer) {
        return 0; // Essay items are to be graded manually
    }
}
