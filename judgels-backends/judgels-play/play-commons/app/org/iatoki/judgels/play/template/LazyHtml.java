package org.iatoki.judgels.play.template;

import com.google.common.collect.Lists;
import play.twirl.api.Html;

import java.util.List;

public final class LazyHtml {

    private final Html baseContent;
    private final List<HtmlTransformation> transformations;

    public LazyHtml(Html baseContent) {
        this.baseContent = baseContent;
        this.transformations = Lists.newArrayList();
    }

    public void appendLayout(HtmlTransformation transformation) {
        transformations.add(transformation);
    }

    public Html render() {
        return render(0);
    }

    public Html render(int level) {
        Html content = baseContent;

        for (HtmlTransformation transformation : transformations) {
            content = transformation.apply(content);
        }

        return content;
    }
}
