package judgels.michael.problem.base;

import java.util.List;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.problem.base.tag.ProblemTags;

public class EditProblemView extends TemplateView {
    public EditProblemView(HtmlTemplate template, EditProblemForm form) {
        super("editProblemView.ftl", template, form);
    }

    public List<String> getTopicTags() {
        return ProblemTags.TOPIC_TAGS;
    }
}
