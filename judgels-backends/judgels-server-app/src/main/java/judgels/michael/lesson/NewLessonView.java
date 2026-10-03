package judgels.michael.lesson;

import java.util.Map;
import judgels.catalog.WorldLanguageRegistry;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class NewLessonView extends TemplateView {
    public NewLessonView(HtmlTemplate template, NewLessonForm form) {
        super("newLessonView.ftl", template, form);
    }

    public Map<String, String> getLanguages() {
        return WorldLanguageRegistry.getInstance().getLanguages();
    }
}
