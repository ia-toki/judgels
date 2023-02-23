package judgels.michael.problem.base;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class CreateProblemForm extends HtmlForm {
    @FormParam("slug")
    String slug;

    @FormParam("gradingEngine")
    String gradingEngine;

    @FormParam("additionalNote")
    String additionalNote;

    @FormParam("initialLanguage")
    String initialLanguage;

    public String getSlug() {
        return slug;
    }

    public String getGradingEngine() {
        return gradingEngine;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public String getInitialLanguage() {
        return initialLanguage;
    }
}
