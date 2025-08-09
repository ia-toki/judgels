package judgels.michael.resource;

import jakarta.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class EditStatementForm extends HtmlForm {
    @FormParam("title")
    public String title;

    @FormParam("text")
    public String text;

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
