package judgels.michael.problem.bundle.statement;

import static java.util.stream.Collectors.toList;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.bundle.BaseBundleProblemResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.ItemConfig;

@Path("/problems/bundle/{problemId}/statements")
public class BundleProblemStatementResource extends BaseBundleProblemResource {
    @Inject public BundleProblemStatementResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewStatement(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Set<String> enabledLanguages = statementStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);
        String defaultLanguage = statementStore.getStatementDefaultLanguage(actor.getUserJid(), problem.getJid());
        ProblemStatement statement = statementStore.getStatement(actor.getUserJid(), problem.getJid(), language);

        List<BundleItem> items = itemStore.getNumberedItems(actor.getUserJid(), problem.getJid());
        List<ItemConfig> itemConfigs = items.stream()
                .map(item -> itemStore.getItemConfig(actor.getUserJid(), problem.getJid(), item, language, defaultLanguage))
                .collect(toList());

        String reasonNotAllowedToSubmit = roleChecker.canSubmit(actor, problem).orElse("");
        boolean canSubmit = reasonNotAllowedToSubmit.isEmpty();

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("view");
        return new ViewStatementView(template, statement, items, itemConfigs, language, enabledLanguages, reasonNotAllowedToSubmit, canSubmit);
    }
}
