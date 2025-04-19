package judgels.michael.lesson;

import jakarta.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class EditLessonForm extends HtmlForm {
    @FormParam("slug")
    String slug;

    @FormParam("additionalNote")
    String additionalNote;

    public String getSlug() {
        return slug;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }
}
