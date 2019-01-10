package org.iatoki.judgels.play;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import play.api.mvc.Call;
import play.twirl.api.Html;

import java.util.List;

/**
 * @deprecated Has been restructured to different package.
 */
@Deprecated
public final class HtmlTemplate {

    private LazyHtml content;
    private String pageTitle;
    private String mainTitle;
    private final List<InternalLink> breadcrumbLocations;
    private final List<InternalLink> sidebarMenus;
    private final List<InternalLink> categoryTabs;
    private final List<InternalLink> mainTabs;
    private final List<InternalLink> secondaryTabs;
    private final List<InternalLink> mainButtons;
    private final List<Html> upperSidebarWidgets;
    private final List<Html> lowerSidebarWidgets;
    private InternalLink mainBackButton;
    private boolean singleColumn;

    public HtmlTemplate() {
        this.breadcrumbLocations = Lists.newArrayList();
        this.sidebarMenus = Lists.newArrayList();
        this.categoryTabs = Lists.newArrayList();
        this.mainTabs = Lists.newArrayList();
        this.secondaryTabs = Lists.newArrayList();
        this.mainButtons = Lists.newArrayList();
        this.upperSidebarWidgets = Lists.newArrayList();
        this.lowerSidebarWidgets = Lists.newArrayList();
        this.singleColumn = false;
    }

    public void setContent(Html content) {
        this.content = new LazyHtml(content);
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

    public void markBreadcrumbLocation(String label, Call target) {
        breadcrumbLocations.add(new InternalLink(label, target));
    }

    public List<InternalLink> getBreadcrumbLinks() {
        return ImmutableList.copyOf(breadcrumbLocations);
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

    public void addMainButton(String label, Call target) {
        mainButtons.add(new InternalLink(label, target));
    }

    public List<InternalLink> getMainButtons() {
        return ImmutableList.copyOf(mainButtons);
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
        return upperSidebarWidgets;
    }

    public void addLowerSidebarWidget(Html widget) {
        lowerSidebarWidgets.add(widget);
    }

    public List<Html> getLowerSidebarWidgets() {
        return lowerSidebarWidgets;
    }
}
