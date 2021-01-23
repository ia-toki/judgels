package org.iatoki.judgels.play.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import play.api.mvc.Call;
import play.mvc.Http;
import play.twirl.api.Html;

public final class HtmlTemplate {
    private Http.Request req;
    private LazyHtml content;
    private String pageTitle;
    private String mainTitle;
    private String secondaryTitle;
    private String description;
    private Html warning;
    private final List<InternalLink> breadcrumbLocations;
    private final List<InternalLink> sidebarMenus;
    private final List<InternalLink> categoryTabs;
    private final List<InternalLink> mainTabs;
    private final List<InternalLink> secondaryTabs;
    private final List<InternalLink> tertiaryTabs;
    private final List<InternalLink> mainButtons;
    private final List<InternalLink> secondaryButtons;
    private final List<Html> upperSidebarWidgets;
    private final List<Html> lowerSidebarWidgets;
    private final List<Html> additionalScripts;
    private InternalLink mainBackButton;
    private boolean singleColumn;
    private boolean reverseBreadcrumbs;

    public HtmlTemplate(Http.Request req) {
        this.req = req;
        this.breadcrumbLocations = Lists.newArrayList();
        this.sidebarMenus = Lists.newArrayList();
        this.categoryTabs = Lists.newArrayList();
        this.mainTabs = Lists.newArrayList();
        this.secondaryTabs = Lists.newArrayList();
        this.tertiaryTabs = Lists.newArrayList();
        this.mainButtons = Lists.newArrayList();
        this.secondaryButtons = Lists.newArrayList();
        this.upperSidebarWidgets = Lists.newArrayList();
        this.lowerSidebarWidgets = Lists.newArrayList();
        this.additionalScripts = Lists.newArrayList();
        this.singleColumn = false;
        this.reverseBreadcrumbs = false;
    }

    public Http.Request getRequest() {
        return req;
    }

    public void setContent(Html content) {
        this.content = new LazyHtml(content);
    }

    public void transformContent(HtmlTransformation transformation) {
        this.content.appendLayout(transformation);
    }

    public LazyHtml getContent() {
        return content;
    }

    public void setSingleColumn() {
        this.singleColumn = true;
    }

    public void setTwoColumns() {
        this.singleColumn = false;
    }

    public boolean isSingleColumn() {
        return singleColumn;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
        this.pageTitle = mainTitle;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public boolean hasMainTitle() {
        return mainTitle != null;
    }

    public void setSecondaryTitle(String mainTitle) {
        this.secondaryTitle = mainTitle;
    }

    public String getSecondaryTitle() {
        return secondaryTitle;
    }

    public boolean hasSecondaryTitle() {
        return secondaryTitle != null;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public void setWarning(Html warning) {
        this.warning = warning;
    }

    public Html getWarning() {
        return warning;
    }

    public boolean hasWarning() {
        return warning != null;
    }

    public void markBreadcrumbLocation(String label, Call target) {
        breadcrumbLocations.add(new InternalLink(label, target));
    }

    public List<InternalLink> getBreadcrumbLinks() {
        List<InternalLink> breadcrumbLocationsCopy = ImmutableList.copyOf(this.breadcrumbLocations);
        if (reverseBreadcrumbs) {
            breadcrumbLocationsCopy = Lists.reverse(breadcrumbLocationsCopy);
        }
        return breadcrumbLocationsCopy;
    }

    public void addSidebarMenu(String label, Call target) {
        sidebarMenus.add(new InternalLink(label, target));
    }

    public List<InternalLink> getSidebarMenus() {
        return ImmutableList.copyOf(sidebarMenus);
    }

    public void addCategoryTab(String label, Call target) {
        categoryTabs.add(new InternalLink(label, target));
    }

    public List<InternalLink> getCategoryTabs() {
        return ImmutableList.copyOf(categoryTabs);
    }

    public boolean hasCategoryTabs() {
        return !categoryTabs.isEmpty();
    }

    public void addMainTab(String label, Call target) {
        mainTabs.add(new InternalLink(label, target));
    }

    public List<InternalLink> getMainTabs() {
        return ImmutableList.copyOf(mainTabs);
    }

    public boolean hasMainTabs() {
        return !mainTabs.isEmpty();
    }

    public void addSecondaryTab(String label, Call target) {
        secondaryTabs.add(new InternalLink(label, target));
    }

    public List<InternalLink> getSecondaryTabs() {
        return ImmutableList.copyOf(secondaryTabs);
    }

    public boolean hasSecondaryTabs() {
        return !secondaryTabs.isEmpty();
    }

    public void addTertiaryTab(String label, Call target) {
        tertiaryTabs.add(new InternalLink(label, target));
    }

    public List<InternalLink> getTertiaryTabs() {
        return ImmutableList.copyOf(tertiaryTabs);
    }

    public boolean hasTertiaryTabs() {
        return !tertiaryTabs.isEmpty();
    }

    public void addMainButton(String label, Call target) {
        mainButtons.add(new InternalLink(label, target));
    }

    public List<InternalLink> getMainButtons() {
        return ImmutableList.copyOf(mainButtons);
    }

    public void addSecondaryButton(String label, Call target) {
        secondaryButtons.add(new InternalLink(label, target));
    }

    public List<InternalLink> getSecondaryButtons() {
        return ImmutableList.copyOf(secondaryButtons);
    }

    public void setMainBackButton(String label, Call target) {
        this.mainBackButton = new InternalLink(label, target);
    }

    public InternalLink getMainBackButton() {
        return mainBackButton;
    }

    public void addUpperSidebarWidget(Html widget) {
        upperSidebarWidgets.add(widget);
    }

    public List<Html> getUpperSidebarWidgets() {
        return ImmutableList.copyOf(upperSidebarWidgets);
    }

    public void addLowerSidebarWidget(Html widget) {
        lowerSidebarWidgets.add(widget);
    }

    public List<Html> getLowerSidebarWidgets() {
        return ImmutableList.copyOf(lowerSidebarWidgets);
    }

    public void addAdditionalScript(Html script) {
        additionalScripts.add(script);
    }

    public List<Html> getAdditionalScripts() {
        return ImmutableList.copyOf(additionalScripts);
    }
}
