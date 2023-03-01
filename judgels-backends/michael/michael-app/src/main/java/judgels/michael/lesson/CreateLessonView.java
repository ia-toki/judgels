package judgels.michael.lesson;

import java.util.Map;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class CreateLessonView extends TemplateView {
    public CreateLessonView(HtmlTemplate template, CreateLessonForm form) {
        super("createLessonView.ftl", template, form);
    }

    public Map<String, String> getLanguages() {
        return WorldLanguageRegistry.getInstance().getLanguages();
    }
}
