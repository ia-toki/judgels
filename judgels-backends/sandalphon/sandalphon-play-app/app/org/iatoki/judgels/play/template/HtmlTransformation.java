package org.iatoki.judgels.play.template;

import java.util.function.Function;
import play.twirl.api.Html;

public interface HtmlTransformation extends Function<Html, Html> {
    // empty
}
