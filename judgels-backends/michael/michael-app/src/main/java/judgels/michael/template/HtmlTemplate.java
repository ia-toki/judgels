package judgels.michael.template;

import java.util.ArrayList;
import java.util.List;

public class HtmlTemplate {
    private final String name;

    private String title = "";
    private String username = "";
    private String avatarUrl = "";
    private List<InternalLink> sidebarMenus = new ArrayList<>();
    private String activeSidebarMenu = "";
    private List<InternalLink> mainTabs = new ArrayList<>();
    private List<InternalLink> mainButtons = new ArrayList<>();
    private SearchProblemsWidget searchProblemsWidget = new SearchProblemsWidget(0, "");
    private boolean hasSearchProblemsWidget;

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

    public void addSidebarMenu(String label, String target) {
        sidebarMenus.add(new InternalLink(label, target));
    }

    public String getActiveSidebarMenu() {
        return activeSidebarMenu;
    }

    public void setActiveSidebarMenu(String activeSidebarMenu) {
        this.activeSidebarMenu = activeSidebarMenu;
    }

    public void addMainTab(String label, String target) {
        mainTabs.add(new InternalLink(label, target));
    }

    public List<InternalLink> getMainTabs() {
        return mainTabs;
    }

    public void addMainButton(String label, String target) {
        mainButtons.add(new InternalLink(label, target));
    }

    public List<InternalLink> getMainButtons() {
        return mainButtons;
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
}
