package judgels.michael.template;

import java.util.ArrayList;
import java.util.List;

public class HtmlTemplate {
    private final String name;

    private String title = "";
    private String username = "";
    private String avatarUrl = "";
    private String globalFormErrorMessage = "";
    private List<InternalLink> sidebarMenus = new ArrayList<>();
    private String activeSidebarMenu = "";

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

    public String getGlobalFormErrorMessage() {
        return globalFormErrorMessage;
    }

    public void setGlobalFormErrorMessage(String globalFormErrorMessage) {
        this.globalFormErrorMessage = globalFormErrorMessage;
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
}
