package org.iatoki.judgels.sandalphon.problem.bundle.item;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import java.io.IOException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.base.ProblemStore;
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

    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final BundleItemStore bundleItemStore;

    @Inject
    public BundleItemController(
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            BundleItemStore bundleItemStore) {

        super(problemStore, problemRoleChecker);
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
        this.bundleItemStore = bundleItemStore;
    }

    @Transactional(readOnly = true)
    public Result viewItems(Http.Request req, long problemId)  {
        return listCreateItems(req, problemId, 0, "id", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listCreateItems(Http.Request req, long problemId, long pageIndex, String orderBy, String orderDir, String filterString) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        try {
            Page<BundleItem> pageOfBundleItems = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, pageIndex, PAGE_SIZE, orderBy, orderDir, filterString);
            Form<ItemCreateForm> itemCreateForm = formFactory.form(ItemCreateForm.class);

            return showListCreateItems(req, problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createItem(Http.Request req, long problemId, String itemType, long page, String orderBy, String orderDir, String filterString) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        if (!EnumUtils.isValidEnum(BundleItemType.class, itemType)) {
            Form<ItemCreateForm> itemCreateForm = formFactory.form(ItemCreateForm.class);

            Page<BundleItem> pageOfBundleItems;
            try {
                pageOfBundleItems = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return showListCreateItems(req, problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm.withGlobalError("Item undefined."));
        }

        BundleItemConfAdapter adapter = BundleItemConfAdapters.fromItemType(BundleItemType.valueOf(itemType));
        if (adapter == null) {
            Form<ItemCreateForm> itemCreateForm = formFactory.form(ItemCreateForm.class).withGlobalError("Item undefined.");

            Page<BundleItem> pageOfBundleItems;
            try {
                pageOfBundleItems = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return showListCreateItems(req, problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        }

        return showCreateItem(req, problem, itemType, adapter.getConfHtml(adapter.generateForm(formFactory), routes.BundleItemController.postCreateItem(problem.getId(), itemType, page, orderBy, orderDir, filterString), "Create"), page, orderBy, orderDir, filterString);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateItem(Http.Request req, long problemId, String itemType, long page, String orderBy, String orderDir, String filterString) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        if (!EnumUtils.isValidEnum(BundleItemType.class, itemType)) {
            Form<ItemCreateForm> itemCreateForm = formFactory.form(ItemCreateForm.class).withGlobalError("Item undefined.");

            Page<BundleItem> pageOfBundleItems;
            try {
                pageOfBundleItems = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return showListCreateItems(req, problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        }

        String language = getStatementLanguage(req, problem);

        BundleItemConfAdapter bundleItemConfAdapter = BundleItemConfAdapters.fromItemType(BundleItemType.valueOf(itemType));
        if (bundleItemConfAdapter == null) {
            Form<ItemCreateForm> itemCreateForm = formFactory.form(ItemCreateForm.class).withGlobalError("Item undefined");

            Page<BundleItem> pageOfBundleItems;
            try {
                pageOfBundleItems = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return showListCreateItems(req, problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        }

        Form bundleItemConfForm = bundleItemConfAdapter.bindFormFromRequest(formFactory, req);
        if (formHasErrors(bundleItemConfForm)) {
            return showCreateItem(req, problem, itemType, bundleItemConfAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postCreateItem(problem.getId(), itemType, page, orderBy, orderDir, filterString), "Create"), page, orderBy, orderDir, filterString);
        }

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        try {
            if (bundleItemStore.bundleItemExistsInProblemWithCloneByMeta(problem.getJid(), actorJid, bundleItemConfAdapter.getMetaFromForm(bundleItemConfForm))) {
                Page<BundleItem> items = bundleItemStore.getPageOfBundleItemsInProblemWithClone(problem.getJid(), actorJid, page, PAGE_SIZE, orderBy, orderDir, filterString);

                return showListCreateItems(req, problem, items, orderBy, orderDir, filterString, bundleItemConfForm.withGlobalError("Duplicate meta on item."));
            }

            bundleItemStore.createBundleItem(problem.getJid(), actorJid, BundleItemType.valueOf(itemType), bundleItemConfAdapter.getMetaFromForm(bundleItemConfForm), bundleItemConfAdapter.processRequestForm(bundleItemConfForm), problemStore
                    .getDefaultLanguage(actorJid, problem.getJid()));

            return redirect(routes.BundleItemController.viewItems(problem.getId()))
                    .addingToSession(req, newCurrentStatementLanguage(language));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editItem(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToUpdateItemInLanguage(req, problem, language));

        try {
            if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
                return notFound();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BundleItem bundleItem;
        try {
            bundleItem = bundleItemStore.findInProblemWithCloneByItemJid(problem.getJid(), actorJid, itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BundleItemConfAdapter bundleItemConfAdapter = BundleItemConfAdapters.fromItemType(bundleItem.getType());
        Set<String> allowedLanguages = problemRoleChecker.getAllowedLanguagesToUpdate(req, problem);

        if (bundleItemConfAdapter == null) {
            return notFound();
        }

        Form bundleItemConfForm;
        try {
            bundleItemConfForm = bundleItemConfAdapter.generateForm(formFactory, bundleItemStore.getItemConfInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid, language), bundleItem.getMeta());
        } catch (IOException e) {
            try {
                bundleItemConfForm = bundleItemConfAdapter.generateForm(formFactory, bundleItemStore.getItemConfInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid, problemStore
                        .getDefaultLanguage(actorJid, problem.getJid())), bundleItem.getMeta());
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }

        return showEditItem(req, problem, language, bundleItem, bundleItemConfAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postEditItem(problem.getId(), itemJid), "Update"), allowedLanguages)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditItem(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToUpdateItemInLanguage(req, problem, language));

        try {
            if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
                return notFound();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BundleItem bundleItem;
        try {
            bundleItem = bundleItemStore.findInProblemWithCloneByItemJid(problem.getJid(), actorJid, itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BundleItemConfAdapter bundleItemConfAdapter = BundleItemConfAdapters.fromItemType(bundleItem.getType());
        Set<String> allowedLanguages = problemRoleChecker.getAllowedLanguagesToUpdate(req, problem);

        if (bundleItemConfAdapter == null) {
            return notFound();
        }

        Form bundleItemConfForm = bundleItemConfAdapter.bindFormFromRequest(formFactory, req);
        if (formHasErrors(bundleItemConfForm)) {
            return showEditItem(req, problem, language, bundleItem, bundleItemConfAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postEditItem(problem.getId(), itemJid), "Update"), allowedLanguages);
        }

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());
        try {
            bundleItemStore.updateBundleItem(problem.getJid(), actorJid, itemJid, bundleItemConfAdapter.getMetaFromForm(bundleItemConfForm), bundleItemConfAdapter.processRequestForm(bundleItemConfForm), language);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return redirect(routes.BundleItemController.viewItems(problem.getId()))
                .addingToSession(req, newCurrentStatementLanguage(language));
    }

    @Transactional(readOnly = true)
    public Result moveItemUp(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        try {
            if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
                return notFound();
            }

            bundleItemStore.moveBundleItemUp(problem.getJid(), actorJid, itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result moveItemDown(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        try {
            if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
                return notFound();
            }

            bundleItemStore.moveBundleItemDown(problem.getJid(), actorJid, itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    @Transactional
    public Result removeItem(Http.Request req, long problemId, String itemJid) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageItems(req, problem));

        problemStore.createUserCloneIfNotExists(actorJid, problem.getJid());

        try {
            if (!bundleItemStore.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), actorJid, itemJid)) {
                return notFound();
            }
            bundleItemStore.removeBundleItem(problem.getJid(), actorJid, itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    private Result showListCreateItems(Http.Request req, Problem problem, Page<BundleItem> pageOfBundleItems, String orderBy, String orderDir, String filterString, Form<ItemCreateForm> itemCreateForm) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listCreateItemsView.render(pageOfBundleItems, problem.getId(), pageOfBundleItems.getPageIndex(), orderBy, orderDir, filterString, itemCreateForm));

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

    private Result showEditItem(Http.Request req, Problem problem, String language, BundleItem bundleItem, Html html, Set<String> allowedLanguages) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(html);
        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));
        template.markBreadcrumbLocation("Update item", routes.BundleItemController.editItem(problem.getId(), bundleItem.getJid()));
        template.setPageTitle("Problem - Bundle - Item - Update");

        return renderTemplate(template, problem);
    }

    protected Result renderTemplate(HtmlTemplate template, Problem problem) {
        template.markBreadcrumbLocation("Items", org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToItems(problem.getId()));

        return super.renderTemplate(template, problem);
    }
}
