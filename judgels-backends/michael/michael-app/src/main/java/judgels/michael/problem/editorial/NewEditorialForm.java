package judgels.michael.problem.editorial;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class NewEditorialForm extends HtmlForm {
    @FormParam("initialLanguage")
    String initialLanguage;

    public String getInitialLanguage() {
        return initialLanguage;
    }
}
