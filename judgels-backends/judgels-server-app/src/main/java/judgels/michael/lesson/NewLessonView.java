package judgels.michael.lesson;

import java.util.Map;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.resource.WorldLanguageRegistry;

public class NewLessonView extends TemplateView {
    public NewLessonView(HtmlTemplate template, NewLessonForm form) {
        super("newLessonView.ftl", template, form);
    }

    public Map<String, String> getLanguages() {
        return WorldLanguageRegistry.getInstance().getLanguages();
    }
}
