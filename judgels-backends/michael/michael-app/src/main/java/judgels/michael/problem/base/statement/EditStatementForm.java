package judgels.michael.problem.base.statement;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class EditStatementForm extends HtmlForm {
    @FormParam("title")
    String title;

    @FormParam("text")
    String text;

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
