package judgels.michael.problem.base.editorial;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class CreateEditorialForm extends HtmlForm {
    @FormParam("initialLanguage")
    String initialLanguage;

    public String getInitialLanguage() {
        return initialLanguage;
    }
}
