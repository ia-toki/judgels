package judgels.michael.template;

public class HtmlTemplate {
    private final String name;

    private String title = "";
    private String globalFormErrorMessage = "";

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

    public String getGlobalFormErrorMessage() {
        return globalFormErrorMessage;
    }

    public void setGlobalFormErrorMessage(String globalFormErrorMessage) {
        this.globalFormErrorMessage = globalFormErrorMessage;
    }
}
