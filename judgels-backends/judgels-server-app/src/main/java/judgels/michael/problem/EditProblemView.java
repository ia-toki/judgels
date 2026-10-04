package judgels.michael.problem;

import java.util.List;
import judgels.catalog.problem.tag.ProblemTags;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class EditProblemView extends TemplateView {
    public EditProblemView(HtmlTemplate template, EditProblemForm form) {
        super("editProblemView.ftl", template, form);
    }

    public List<String> getTopicTags() {
        return ProblemTags.TOPIC_TAGS;
    }
}
