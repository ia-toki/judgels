package org.iatoki.judgels.play;

import com.google.inject.Inject;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.iatoki.judgels.play.banner.BannerConfig;
import org.iatoki.judgels.play.controllers.EntityNotFoundGuard;
import org.iatoki.judgels.play.controllers.UnsupportedOperationGuard;
import org.iatoki.judgels.play.general.GeneralConfig;
import org.iatoki.judgels.play.google.analytics.GoogleAnalyticsConfig;
import org.iatoki.judgels.play.seo.SeoConfig;
import org.iatoki.judgels.play.sponsor.SponsorConfig;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.play.template.LazyHtml;
import org.iatoki.judgels.play.template.base.html.baseLayout;
import org.iatoki.judgels.play.template.base.html.breadcrumbsLayout;
import org.iatoki.judgels.play.template.base.html.headerFooterLayout;
import org.iatoki.judgels.play.template.base.html.singleColumnLayout;
import org.iatoki.judgels.play.template.base.html.twoColumnLayout;
import org.iatoki.judgels.play.template.content.html.categoryTabsLayout;
import org.iatoki.judgels.play.template.content.html.contentLayout;
import org.iatoki.judgels.play.template.content.html.descriptionLayout;
import org.iatoki.judgels.play.template.content.html.mainTabsLayout;
import org.iatoki.judgels.play.template.content.html.mainTitleLayout;
import org.iatoki.judgels.play.template.content.html.scriptsLayout;
import org.iatoki.judgels.play.template.content.html.secondaryTabsLayout;
import play.api.mvc.Call;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Html;

@EntityNotFoundGuard
@UnsupportedOperationGuard
public abstract class AbstractJudgelsController extends Controller {

    @Inject
    protected GeneralConfig generalConfig;

    @Inject(optional = true)
    protected BannerConfig bannerConfig;

    @Inject(optional = true)
    protected SponsorConfig sponsorConfig;

    @Inject(optional = true)
    protected SeoConfig seoConfig;

    @Inject(optional = true)
    protected GoogleAnalyticsConfig googleAnalyticsConfig;

    protected static void flashInfo(String message) {
        flash("flashInfo", message);
    }

    protected static void flashError(String message) {
        flash("flashError", message);
    }

    protected static boolean formHasErrors(Form form) {
        return form.hasErrors() || form.hasGlobalErrors();
    }

    protected static Result redirectToReferer() {
        return redirect(request().getHeader(REFERER));
    }

    protected static Result lazyOk(LazyHtml content) {
        return getResult(content, Http.Status.OK);
    }

    protected static Result getResult(LazyHtml content, int statusCode) {
        HtmlCompressor htmlCompressor = new HtmlCompressor();
        Html compressedContent = new Html(htmlCompressor.compress(content.render().body()));
        switch (statusCode) {
            case Http.Status.OK:
                return Results.ok(compressedContent);
            case Http.Status.NOT_FOUND:
                return Results.notFound(compressedContent);
            default:
                return Results.badRequest(compressedContent);
        }
    }

    protected static String getCurrentUserIpAddress() {
        return request().remoteAddress();
    }

    protected static String getAbsoluteUrl(Call call) {
        return call.absoluteURL(request(), request().secure());
    }

    protected HtmlTemplate getBaseHtmlTemplate() {
        return new HtmlTemplate();
    }

    protected Result renderTemplate(HtmlTemplate template) {
        LazyHtml content = template.getContent();

        if (template.hasSecondaryTabs()) {
            content.appendLayout(c -> secondaryTabsLayout.render(template.getSecondaryTabs(), c));
        }

        if (template.hasMainTabs()) {
            content.appendLayout(c -> mainTabsLayout.render(template.getMainTabs(), c));
        }

        if (template.hasDescription()) {
            content.appendLayout(c -> descriptionLayout.render(template.getDescription(), c));
        }

        if (template.hasMainTitle()) {
            content.appendLayout(c -> mainTitleLayout.render(template.getMainTitle(), template.getMainButtons(), template.getMainBackButton(), c));
        }

        if (template.hasCategoryTabs()) {
            content.appendLayout(c -> categoryTabsLayout.render(template.getCategoryTabs(), c));
        }

        content.appendLayout(c -> contentLayout.render(c));

        if (template.isSingleColumn()) {
            content.appendLayout(c -> singleColumnLayout.render(c));
        } else {
            content.appendLayout(c -> twoColumnLayout.render(template.getSidebarMenus(), template.getUpperSidebarWidgets(), template.getLowerSidebarWidgets(), isSidebarOnTheLeft(), isSidebarVisible(), c));
        }

        content.appendLayout(c -> breadcrumbsLayout.render(template.getBreadcrumbLinks(), bannerConfig, c));
        content.appendLayout(c -> headerFooterLayout.render(generalConfig, sponsorConfig, c));
        content.appendLayout(c -> baseLayout.render(template.getPageTitle(), generalConfig, seoConfig, googleAnalyticsConfig, c));
        content.appendLayout(c -> scriptsLayout.render(template.getAdditionalScripts(), c));

        return lazyOk(content);
    }

    private boolean isSidebarOnTheLeft() {
        return !Context.current().args.containsKey("sidebar.isAfter");
    }

    private boolean isSidebarVisible() {
        return request().cookie("sidebar") == null || request().cookie("sidebar").value().equals("true");
    }
}
