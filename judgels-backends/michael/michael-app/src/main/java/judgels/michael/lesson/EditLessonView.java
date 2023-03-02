package judgels.michael.lesson;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditLessonView extends TemplateView {
    public EditLessonView(HtmlTemplate template, EditLessonForm form) {
        super("editLessonView.ftl", template, form);
    }
}
