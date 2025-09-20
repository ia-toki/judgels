package judgels.michael.problem.bundle.item;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.common.View;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.bundle.BaseBundleProblemResource;
import judgels.michael.problem.bundle.item.config.ItemConfigAdapter;
import judgels.michael.problem.bundle.item.config.ItemConfigAdapterRegistry;
import judgels.michael.problem.bundle.item.config.ItemConfigForm;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.problem.bundle.item.ItemEngineRegistry;

@Path("/problems/bundle/{problemId}/items")
public class BundleProblemItemResource extends BaseBundleProblemResource {
    @Inject public BundleProblemItemResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listItems(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String defaultLanguage = statementStore.getStatementDefaultLanguage(actor.getUserJid(), problem.getJid());

        List<BundleItem> items = itemStore.getNumberedItems(actor.getUserJid(), problem.getJid());
        Map<String, ItemConfig> itemConfigs = items.stream().collect(Collectors.toMap(
                item -> item.getJid(),
                item -> itemStore.getItemConfig(actor.getUserJid(), problem.getJid(), item, defaultLanguage, defaultLanguage)));

        HtmlTemplate template = newProblemItemTemplate(actor, problem);
        return new ListItemsView(template, items, itemConfigs, roleChecker.canEdit(actor, problem));
    }

    @POST
    @UnitOfWork
    public Response createItem(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @FormParam("type") String type) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));

        ItemType itemType = ItemType.valueOf(type);
        ItemConfig itemConfig = ItemEngineRegistry.getByType(itemType).createDefaultConfig();

        String defaultLanguage = statementStore.getStatementDefaultLanguage(actor.getUserJid(), problem.getJid());

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        BundleItem item = itemStore.createItem(actor.getUserJid(), problem.getJid(), itemType, itemConfig, defaultLanguage);

        return redirect("/problems/bundle/" + problemId + "/items/" + item.getJid());
    }

    @GET
    @Path("/{itemJid}")
    @UnitOfWork(readOnly = true)
    public View editItem(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("itemJid") String itemJid) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        BundleItem item = checkFound(itemStore.getNumberedItem(actor.getUserJid(), problem.getJid(), itemJid));

        Set<String> enabledLanguages = statementStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);
        String defaultLanguage = statementStore.getStatementDefaultLanguage(actor.getUserJid(), problem.getJid());

        ItemConfig config = itemStore.getItemConfig(actor.getUserJid(), problem.getJid(), item, language, defaultLanguage);

        ItemConfigAdapter adapter = ItemConfigAdapterRegistry.getByType(item.getType());
        ItemConfigForm form = adapter.buildFormFromConfig(config);

        form.meta = item.getMeta();

        HtmlTemplate template = newProblemItemTemplate(actor, problem);
        return new EditItemView(item, template, form, language, enabledLanguages, roleChecker.canEdit(actor, problem));
    }

    @POST
    @Path("/{itemJid}")
    @UnitOfWork
    public Response updateItem(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("itemJid") String itemJid,
            @BeanParam ItemConfigForm form) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        BundleItem item = checkFound(itemStore.getNumberedItem(actor.getUserJid(), problem.getJid(), itemJid));

        Set<String> enabledLanguages = statementStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);

        ItemConfigAdapter adapter = ItemConfigAdapterRegistry.getByType(item.getType());
        ItemConfig config = adapter.buildConfigFromForm(form);

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        itemStore.updateItem(actor.getUserJid(), problem.getJid(), item, form.meta, config, language);

        return redirect("/problems/bundle/" + problemId + "/items/" + itemJid);
    }

    @GET
    @Path("/{itemJid}/up")
    @UnitOfWork
    public Response moveItemUp(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("itemJid") String itemJid) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));
        checkFound(itemStore.getNumberedItem(actor.getUserJid(), problem.getJid(), itemJid));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        itemStore.moveItemUp(actor.getUserJid(), problem.getJid(), itemJid);

        return redirect("/problems/bundle/" + problemId + "/items/" + itemJid);
    }

    @GET
    @Path("/{itemJid}/down")
    @UnitOfWork
    public Response moveItemDown(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("itemJid") String itemJid) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));
        checkFound(itemStore.getNumberedItem(actor.getUserJid(), problem.getJid(), itemJid));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        itemStore.moveItemDown(actor.getUserJid(), problem.getJid(), itemJid);

        return redirect("/problems/bundle/" + problemId + "/items");
    }

    @GET
    @Path("/{itemJid}/remove")
    @UnitOfWork
    public Response removeItem(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("itemJid") String itemJid) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canEdit(actor, problem));
        checkFound(itemStore.getNumberedItem(actor.getUserJid(), problem.getJid(), itemJid));

        problemStore.createUserCloneIfNotExists(actor.getUserJid(), problem.getJid());
        itemStore.removeItem(actor.getUserJid(), problem.getJid(), itemJid);

        return redirect("/problems/bundle/" + problemId + "/items");
    }

    private HtmlTemplate newProblemItemTemplate(Actor actor, Problem problem) {
        HtmlTemplate template = newProblemTemplate(actor, problem);
        template.setActiveMainTab("items");
        return template;
    }
}
