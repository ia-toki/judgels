package org.iatoki.judgels.play.template;

import play.twirl.api.Html;

import java.util.function.Function;

public interface HtmlTransformation extends Function<Html, Html> {
    // empty
}
