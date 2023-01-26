package org.iatoki.judgels.sandalphon.problem.bundle.item;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.problem.base.ProblemStore;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.listCreateItemsView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;

@Singleton
public final class BundleItemController extends AbstractProblemController {
    private static final long PAGE_SIZE = 1000;

    private final ObjectMapper mapper;
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final BundleItemStore bundleItemStore;

    @Inject
    public BundleItemController(
            ObjectMapper mapper,
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            BundleItemStore bundleItemStore) {

        super(problemStore, problemRoleChecker);
        this.mapper = mapper;
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
        this.bundleItemStore = bundleItemStore;
    }

    @Transactional(readOnly = true)
    public Result viewItems(Http.Request req, long problemId)  {
        return listCreateItems(req, problemId, 1, "id", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listCreateItems(Http.Request req, long problemId, long pageIndex, String orderBy, String orderDir, String filterString) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        Page<BundleItem> items = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, pageIndex, PAGE_SIZE, orderBy, orderDir, filterString);
        Form<ItemCreateForm> form = formFactory.form(ItemCreateForm.class);

        return showListCreateItems(req, problem, items, orderBy, orderDir, filterString, form);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createItem(Http.Request req, long problemId, String itemType, long page, String orderBy, String orderDir, String filterString) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        if (!EnumUtils.isValidEnum(ItemType.class, itemType)) {
            Form<ItemCreateForm> form = formFactory.form(ItemCreateForm.class);

            Page<BundleItem> items = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateItems(req, problem, items, orderBy, orderDir, filterString, form.withGlobalError("Item undefined."));
        }

        ItemConfigAdapter adapter = ItemConfigAdapters.fromItemType(ItemType.valueOf(itemType), mapper);
        if (adapter == null) {
            Form<ItemCreateForm> form = formFactory.form(ItemCreateForm.class).withGlobalError("Item undefined.");

            Page<BundleItem> items = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateItems(req, problem, items, orderBy, orderDir, filterString, form);
        }

        return showCreateItem(req, problem, itemType, adapter.getConfHtml(adapter.generateForm(formFactory), routes.BundleItemController.postCreateItem(problem.getId(), itemType, page, orderBy, orderDir, filterString), "Create"), page, orderBy, orderDir, filterString);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateItem(Http.Request req, long problemId, String itemType, long page, String orderBy, String orderDir, String filterString) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        if (!EnumUtils.isValidEnum(ItemType.class, itemType)) {
            Form<ItemCreateForm> form = formFactory.form(ItemCreateForm.class).withGlobalError("Item undefined.");

            Page<BundleItem> items = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateItems(req, problem, items, orderBy, orderDir, filterString, form);
        }

        String language = getStatementLanguage(req, problem);

        ItemConfigAdapter itemConfigAdapter = ItemConfigAdapters.fromItemType(ItemType.valueOf(itemType), mapper);
        if (itemConfigAdapter == null) {
            Form<ItemCreateForm> form = formFactory.form(ItemCreateForm.class).withGlobalError("Item undefined");

            Page<BundleItem> items = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateItems(req, problem, items, orderBy, orderDir, filterString, form);
        }

        Form bundleItemConfForm = itemConfigAdapter.bindFormFromRequest(formFactory, req);
        if (formHasErrors(bundleItemConfForm)) {
            return showCreateItem(req, problem, itemType, itemConfigAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postCreateItem(problem.getId(), itemType, page, orderBy, orderDir, filterString), "Create"), page, orderBy, orderDir, filterString);
        }

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        if (bundleItemStore.bundleItemExistsInProblemWithCloneByMeta(problem.getJid(), actorJid, itemConfigAdapter.getMetaFromForm(bundleItemConfForm))) {
            Page<BundleItem> items = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);

            return showListCreateItems(req, problem, items, orderBy, orderDir, filterString, bundleItemConfForm.withGlobalError("Duplicate meta on item."));
        }

        bundleItemStore.createBundleItem(problem.getJid(), actorJid, ItemType.valueOf(itemType), itemConfigAdapter.getMetaFromForm(bundleItemConfForm), itemConfigAdapter
                .processRequestForm(bundleItemConfForm), problemStore
                .getStatementDefaultLanguage(actorJid, problem.getJid()));

        return redirect(routes.BundleItemController.viewItems(problem.getId()))
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editItem(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToUpdateItemInLanguage(req, problem, language));

        if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
            return notFound();
        }

        BundleItem item = bundleItemStore.findInProblemWithCloneByItemJid(problem.getJid(), actorJid, itemJid);

        ItemConfigAdapter itemConfigAdapter = ItemConfigAdapters.fromItemType(item.getType(), mapper);
        Set<String> allowedLanguages = problemRoleChecker.getAllowedStatementLanguagesToUpdate(req, problem);

        if (itemConfigAdapter == null) {
            return notFound();
        }

        Form bundleItemConfForm;
        try {
            bundleItemConfForm = itemConfigAdapter.generateForm(formFactory, bundleItemStore.getItemConfInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid, language), item.getMeta());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                bundleItemConfForm = itemConfigAdapter.generateForm(formFactory, bundleItemStore.getItemConfInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid, problemStore
                        .getStatementDefaultLanguage(actorJid, problem.getJid())), item.getMeta());
            } else {
                throw e;
            }
        }

        return showEditItem(req, problem, language, item, itemConfigAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postEditItem(problem.getId(), itemJid), "Update"), allowedLanguages)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditItem(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToUpdateItemInLanguage(req, problem, language));

        if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
            return notFound();
        }

        BundleItem item = bundleItemStore.findInProblemWithCloneByItemJid(problem.getJid(), actorJid, itemJid);

        ItemConfigAdapter itemConfigAdapter = ItemConfigAdapters.fromItemType(item.getType(), mapper);
        Set<String> allowedLanguages = problemRoleChecker.getAllowedStatementLanguagesToUpdate(req, problem);

        if (itemConfigAdapter == null) {
            return notFound();
        }

        Form bundleItemConfForm = itemConfigAdapter.bindFormFromRequest(formFactory, req);
        if (formHasErrors(bundleItemConfForm)) {
            return showEditItem(req, problem, language, item, itemConfigAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postEditItem(problem.getId(), itemJid), "Update"), allowedLanguages);
        }

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());
        bundleItemStore.updateBundleItem(problem.getJid(), actorJid, itemJid, itemConfigAdapter.getMetaFromForm(bundleItemConfForm), itemConfigAdapter
                .processRequestForm(bundleItemConfForm), language);

        return redirect(routes.BundleItemController.viewItems(problem.getId()))
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional(readOnly = true)
    public Result moveItemUp(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
            return notFound();
        }

        bundleItemStore.moveBundleItemUp(problem.getJid(), actorJid, itemJid);

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result moveItemDown(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
            return notFound();
        }

        bundleItemStore.moveBundleItemDown(problem.getJid(), actorJid, itemJid);

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    @Transactional
    public Result removeItem(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
            return notFound();
        }
        bundleItemStore.removeBundleItem(problem.getJid(), actorJid, itemJid);

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    private Result showListCreateItems(Http.Request req, Problem problem, Page<BundleItem> items, String orderBy, String orderDir, String filterString, Form<ItemCreateForm> form) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listCreateItemsView.render(items, problem.getId(), items.getPageIndex(), orderBy, orderDir, filterString, form));

        template.setPageTitle("Problem - Bundle - Items");

        return renderTemplate(template, problem);
    }

    private Result showCreateItem(Http.Request req, Problem problem, String itemType, Html html, long page, String orderBy, String orderDir, String filterString) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(html);
        template.markBreadcrumbLocation("Create item", routes.BundleItemController.createItem(problem.getId(), itemType, page, orderBy, orderDir, filterString));
        template.setPageTitle("Problem - Bundle - Items - Create");

        return renderTemplate(template, problem);
    }

    private Result showEditItem(Http.Request req, Problem problem, String language, BundleItem item, Html html, Set<String> allowedLanguages) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(html);
        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));
        template.markBreadcrumbLocation("Update item", routes.BundleItemController.editItem(problem.getId(), item.getJid()));
        template.setPageTitle("Problem - Bundle - Item - Update");

        return renderTemplate(template, problem);
    }

    protected Result renderTemplate(HtmlTemplate template, Problem problem) {
        template.markBreadcrumbLocation("Items", org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToItems(problem.getId()));

        return super.renderTemplate(template, problem);
    }
}
