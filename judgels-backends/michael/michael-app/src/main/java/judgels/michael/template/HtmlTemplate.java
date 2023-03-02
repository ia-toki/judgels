package judgels.michael.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HtmlTemplate {
    private final String name;

    private String title = "";
    private String username = "";
    private String avatarUrl = "";
    private List<InternalLink> sidebarMenus = new ArrayList<>();
    private String activeSidebarMenu = "";
    private List<InternalLink> mainTabs = new ArrayList<>();
    private String activeMainTab = "";
    private List<InternalLink> mainButtons = new ArrayList<>();
    private List<InternalLink> secondaryTabs = new ArrayList<>();
    private String activeSecondaryTab = "";
    private SearchProblemsWidget searchProblemsWidget = new SearchProblemsWidget(0, "", new ArrayList<>(), new HashMap<>());
    private SearchLessonsWidget searchLessonsWidget = new SearchLessonsWidget(0, "");
    private boolean hasSearchProblemsWidget;
    private boolean hasSearchLessonsWidget;

    public HtmlTemplate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<InternalLink> getSidebarMenus() {
        return sidebarMenus;
    }

    public void addSidebarMenu(String key, String label, String target) {
        sidebarMenus.add(new InternalLink(key, label, target));
    }

    public String getActiveSidebarMenu() {
        return activeSidebarMenu;
    }

    public void setActiveSidebarMenu(String activeSidebarMenu) {
        this.activeSidebarMenu = activeSidebarMenu;
    }

    public void addMainTab(String key, String label, String target) {
        mainTabs.add(new InternalLink(key, label, target));
    }

    public List<InternalLink> getMainTabs() {
        return mainTabs;
    }

    public void setActiveMainTab(String activeMainTab) {
        this.activeMainTab = activeMainTab;
    }

    public String getActiveMainTab() {
        return activeMainTab;
    }

    public void addMainButton(String label, String target) {
        mainButtons.add(new InternalLink(label, target));
    }

    public List<InternalLink> getMainButtons() {
        return mainButtons;
    }

    public void addSecondaryTab(String key, String label, String target) {
        secondaryTabs.add(new InternalLink(key, label, target));
    }

    public List<InternalLink> getSecondaryTabs() {
        return secondaryTabs;
    }

    public void setActiveSecondaryTab(String activeSecondaryTab) {
        this.activeSecondaryTab = activeSecondaryTab;
    }

    public String getActiveSecondaryTab() {
        return activeSecondaryTab;
    }

    public SearchProblemsWidget getSearchProblemsWidget() {
        return searchProblemsWidget;
    }

    public void setSearchProblemsWidget(SearchProblemsWidget searchProblemsWidget) {
        this.searchProblemsWidget = searchProblemsWidget;
        this.hasSearchProblemsWidget = true;
    }

    public boolean isHasSearchProblemsWidget() {
        return hasSearchProblemsWidget;
    }

    public SearchLessonsWidget getSearchLessonsWidget() {
        return searchLessonsWidget;
    }

    public void setSearchLessonsWidget(SearchLessonsWidget searchLessonsWidget) {
        this.searchLessonsWidget = searchLessonsWidget;
        this.hasSearchLessonsWidget = true;
    }

    public boolean isHasSearchLessonsWidget() {
        return hasSearchLessonsWidget;
    }
}
