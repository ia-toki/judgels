package judgels.michael.lesson;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class CreateLessonForm extends HtmlForm {
    @FormParam("slug")
    String slug;

    @FormParam("additionalNote")
    String additionalNote;

    @FormParam("initialLanguage")
    String initialLanguage;

    public String getSlug() {
        return slug;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public String getInitialLanguage() {
        return initialLanguage;
    }
}
