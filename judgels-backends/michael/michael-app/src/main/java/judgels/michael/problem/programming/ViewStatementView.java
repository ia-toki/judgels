package judgels.michael.problem.programming;

import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.sandalphon.api.problem.Problem;

public class ViewStatementView extends TemplateView {
    private final Problem problem;

    public ViewStatementView(HtmlTemplate template, Problem problem) {
        super("viewStatementView.ftl", template);
        this.problem = problem;
    }

    public Problem getProblem() {
        return problem;
    }
}
