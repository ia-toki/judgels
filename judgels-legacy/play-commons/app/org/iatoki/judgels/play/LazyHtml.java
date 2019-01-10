package org.iatoki.judgels.play;

import com.google.common.collect.Lists;
import org.iatoki.judgels.play.template.HtmlTransformation;
import play.twirl.api.Html;

import java.util.List;

/**
 * @deprecated Has been restructured to different package.
 */
@Deprecated
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
