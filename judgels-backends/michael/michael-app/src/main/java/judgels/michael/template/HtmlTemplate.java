package judgels.michael.template;

public class HtmlTemplate {
    private final String name;

    private String title = "";

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
}
