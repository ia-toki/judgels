package org.iatoki.judgels.sandalphon.problem.bundle.item;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.EnumUtils;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.bundle.BundleProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.item.html.listCreateItemsView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class BundleItemController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 1000;

    private final BundleItemService bundleItemService;
    private final ProblemService problemService;

    @Inject
    public BundleItemController(BundleItemService bundleItemService, ProblemService problemService) {
        this.bundleItemService = bundleItemService;
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result viewItems(long problemId) throws ProblemNotFoundException  {
        return listCreateItems(problemId, 0, "id", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listCreateItems(long problemId, long pageIndex, String orderBy, String orderDir, String filterString) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToManageItems(problemService, problem)) {
            return notFound();
        }

        try {
            Page<BundleItem> pageOfBundleItems = bundleItemService.getPageOfBundleItemsInProblemWithClone(problem.getJid(), IdentityUtils.getUserJid(), pageIndex, PAGE_SIZE, orderBy, orderDir, filterString);
            Form<ItemCreateForm> itemCreateForm = Form.form(ItemCreateForm.class);

            return showListCreateItems(problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createItem(long problemId, String itemType, long page, String orderBy, String orderDir, String filterString) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToManageItems(problemService, problem)) {
            return notFound();
        }

        if (!EnumUtils.isValidEnum(BundleItemType.class, itemType)) {
            Form<ItemCreateForm> itemCreateForm = Form.form(ItemCreateForm.class);
            itemCreateForm.reject("error.problem.bundle.item.undefined");

            Page<BundleItem> pageOfBundleItems;
            try {
                pageOfBundleItems = bundleItemService.getPageOfBundleItemsInProblemWithClone(problem.getJid(), IdentityUtils.getUserJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return showListCreateItems(problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        }

        BundleItemConfAdapter adapter = BundleItemConfAdapters.fromItemType(BundleItemType.valueOf(itemType));
        if (adapter == null) {
            Form<ItemCreateForm> itemCreateForm = Form.form(ItemCreateForm.class);
            itemCreateForm.reject("error.problem.bundle.item.undefined");

            Page<BundleItem> pageOfBundleItems;
            try {
                pageOfBundleItems = bundleItemService.getPageOfBundleItemsInProblemWithClone(problem.getJid(), IdentityUtils.getUserJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return showListCreateItems(problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        }

        return showCreateItem(problem, itemType, adapter.getConfHtml(adapter.generateForm(), routes.BundleItemController.postCreateItem(problem.getId(), itemType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateItem(long problemId, String itemType, long page, String orderBy, String orderDir, String filterString) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToManageItems(problemService, problem)) {
            return notFound();
        }

        if (!EnumUtils.isValidEnum(BundleItemType.class, itemType)) {
            Form<ItemCreateForm> itemCreateForm = Form.form(ItemCreateForm.class);
            itemCreateForm.reject("error.problem.bundle.item.undefined");

            Page<BundleItem> pageOfBundleItems;
            try {
                pageOfBundleItems = bundleItemService.getPageOfBundleItemsInProblemWithClone(problem.getJid(), IdentityUtils.getUserJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return showListCreateItems(problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        }

        try {
            ProblemControllerUtils.establishStatementLanguage(problemService, problem);
        } catch (IOException e) {
            return notFound();
        }

        BundleItemConfAdapter bundleItemConfAdapter = BundleItemConfAdapters.fromItemType(BundleItemType.valueOf(itemType));
        if (bundleItemConfAdapter == null) {
            Form<ItemCreateForm> itemCreateForm = Form.form(ItemCreateForm.class);
            itemCreateForm.reject("error.problem.bundle.item.undefined");

            Page<BundleItem> pageOfBundleItems;
            try {
                pageOfBundleItems = bundleItemService.getPageOfBundleItemsInProblemWithClone(problem.getJid(), IdentityUtils.getUserJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return showListCreateItems(problem, pageOfBundleItems, orderBy, orderDir, filterString, itemCreateForm);
        }

        Form bundleItemConfForm = bundleItemConfAdapter.bindFormFromRequest(request());
        if (formHasErrors(bundleItemConfForm)) {
            return showCreateItem(problem, itemType, bundleItemConfAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postCreateItem(problem.getId(), itemType, page, orderBy, orderDir, filterString), Messages.get("commons.create")), page, orderBy, orderDir, filterString);
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            if (bundleItemService.bundleItemExistsInProblemWithCloneByMeta(problem.getJid(), IdentityUtils.getUserJid(), bundleItemConfAdapter.getMetaFromForm(bundleItemConfForm))) {
                bundleItemConfForm.reject("error.problem.bundle.item.duplicateMeta");
                Page<BundleItem> items = bundleItemService.getPageOfBundleItemsInProblemWithClone(problem.getJid(), IdentityUtils.getUserJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);

                return showListCreateItems(problem, items, orderBy, orderDir, filterString, bundleItemConfForm);
            }

            bundleItemService.createBundleItem(problem.getJid(), IdentityUtils.getUserJid(), BundleItemType.valueOf(itemType), bundleItemConfAdapter.getMetaFromForm(bundleItemConfForm), bundleItemConfAdapter.processRequestForm(bundleItemConfForm), ProblemControllerUtils.getDefaultStatementLanguage(problemService, problem));

            return redirect(routes.BundleItemController.viewItems(problem.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editItem(long problemId, String itemJid) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToUpdateItemInLanguage(problemService, problem)) {
            return notFound();
        }

        try {
            if (!bundleItemService.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid)) {
                return notFound();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            ProblemControllerUtils.establishStatementLanguage(problemService, problem);
        } catch (IOException e) {
            return notFound();
        }

        BundleItem bundleItem;
        try {
            bundleItem = bundleItemService.findInProblemWithCloneByItemJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BundleItemConfAdapter bundleItemConfAdapter = BundleItemConfAdapters.fromItemType(bundleItem.getType());
        Set<String> allowedLanguages;
        try {
            allowedLanguages = ProblemControllerUtils.getAllowedLanguagesToUpdate(problemService, problem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (bundleItemConfAdapter == null) {
            return notFound();
        }

        Form bundleItemConfForm;
        try {
            bundleItemConfForm = bundleItemConfAdapter.generateForm(bundleItemService.getItemConfInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid, ProblemControllerUtils.getCurrentStatementLanguage()), bundleItem.getMeta());
        } catch (IOException e) {
            try {
                bundleItemConfForm = bundleItemConfAdapter.generateForm(bundleItemService.getItemConfInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid, ProblemControllerUtils.getDefaultStatementLanguage(problemService, problem)), bundleItem.getMeta());
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }

        return showEditItem(problem, bundleItem, bundleItemConfAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postEditItem(problem.getId(), itemJid), Messages.get("commons.update")), allowedLanguages);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditItem(long problemId, String itemJid) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToUpdateItemInLanguage(problemService, problem)) {
            return notFound();
        }

        try {
            if (!bundleItemService.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid)) {
                return notFound();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BundleItem bundleItem;
        try {
            bundleItem = bundleItemService.findInProblemWithCloneByItemJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            ProblemControllerUtils.establishStatementLanguage(problemService, problem);
        } catch (IOException e) {
            return notFound();
        }

        BundleItemConfAdapter bundleItemConfAdapter = BundleItemConfAdapters.fromItemType(bundleItem.getType());
        Set<String> allowedLanguages;
        try {
            allowedLanguages = ProblemControllerUtils.getAllowedLanguagesToUpdate(problemService, problem);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (bundleItemConfAdapter == null) {
            return notFound();
        }

        Form bundleItemConfForm = bundleItemConfAdapter.bindFormFromRequest(request());
        if (formHasErrors(bundleItemConfForm)) {
            return showEditItem(problem, bundleItem, bundleItemConfAdapter.getConfHtml(bundleItemConfForm, routes.BundleItemController.postEditItem(problem.getId(), itemJid), Messages.get("commons.update")), allowedLanguages);
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());
        try {
            bundleItemService.updateBundleItem(problem.getJid(), IdentityUtils.getUserJid(), itemJid, bundleItemConfAdapter.getMetaFromForm(bundleItemConfForm), bundleItemConfAdapter.processRequestForm(bundleItemConfForm), ProblemControllerUtils.getCurrentStatementLanguage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result moveItemUp(long problemId, String itemJid) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToManageItems(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            if (!bundleItemService.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid)) {
                return notFound();
            }

            bundleItemService.moveBundleItemUp(problem.getJid(), IdentityUtils.getUserJid(), itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result moveItemDown(long problemId, String itemJid) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToManageItems(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            if (!bundleItemService.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid)) {
                return notFound();
            }

            bundleItemService.moveBundleItemDown(problem.getJid(), IdentityUtils.getUserJid(), itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    @Transactional
    public Result removeItem(long problemId, String itemJid) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!BundleProblemControllerUtils.isAllowedToManageItems(problemService, problem)) {
            return notFound();
        }

        problemService.createUserCloneIfNotExists(IdentityUtils.getUserJid(), problem.getJid());

        try {
            if (!bundleItemService.bundleItemExistsInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), itemJid)) {
                return notFound();
            }
            bundleItemService.removeBundleItem(problem.getJid(), IdentityUtils.getUserJid(), itemJid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return redirect(routes.BundleItemController.viewItems(problem.getId()));
    }

    private Result showListCreateItems(Problem problem, Page<BundleItem> pageOfBundleItems, String orderBy, String orderDir, String filterString, Form<ItemCreateForm> itemCreateForm) {
        LazyHtml content = new LazyHtml(listCreateItemsView.render(pageOfBundleItems, problem.getId(), pageOfBundleItems.getPageIndex(), orderBy, orderDir, filterString, itemCreateForm));

        BundleProblemControllerUtils.appendTabsLayout(content, problemService, problem);
        ProblemControllerUtils.appendVersionLocalChangesWarningLayout(content, problemService, problem);
        ProblemControllerUtils.appendTitleLayout(content, problemService, problem);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, problem, ImmutableList.of(
              new InternalLink(Messages.get("problem.bundle.item.list"), routes.BundleItemController.viewItems(problem.getId()))
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - Bundle - Items");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showCreateItem(Problem problem, String itemType, Html html, long page, String orderBy, String orderDir, String filterString) {
        LazyHtml content = new LazyHtml(html);
        BundleProblemControllerUtils.appendTabsLayout(content, problemService, problem);
        ProblemControllerUtils.appendVersionLocalChangesWarningLayout(content, problemService, problem);
        ProblemControllerUtils.appendTitleLayout(content, problemService, problem);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, problem, ImmutableList.of(
              new InternalLink(Messages.get("problem.bundle.item.list"), routes.BundleItemController.viewItems(problem.getId())),
              new InternalLink(Messages.get("problem.bundle.item.create"), routes.BundleItemController.createItem(problem.getId(), itemType, page, orderBy, orderDir, filterString))
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - Bundle - Items - Create");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditItem(Problem problem, BundleItem bundleItem, Html html, Set<String> allowedLanguages) {
        LazyHtml content = new LazyHtml(html);
        ProblemControllerUtils.appendStatementLanguageSelectionLayout(content, ProblemControllerUtils.getCurrentStatementLanguage(), allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));
        BundleProblemControllerUtils.appendTabsLayout(content, problemService, problem);
        ProblemControllerUtils.appendVersionLocalChangesWarningLayout(content, problemService, problem);
        ProblemControllerUtils.appendTitleLayout(content, problemService, problem);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, problem, ImmutableList.of(
              new InternalLink(Messages.get("problem.bundle.item.list"), routes.BundleItemController.viewItems(problem.getId())),
              new InternalLink(Messages.get("problem.bundle.item.update"), routes.BundleItemController.editItem(problem.getId(), bundleItem.getJid()))
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - Bundle - Item - Update");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Problem problem, List<InternalLink> lastLinks) {
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
              ProblemControllerUtils.getProblemBreadcrumbsBuilder(problem)
                    .add(new InternalLink(Messages.get("problem.bundle.item"), org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToItems(problem.getId())))
                    .addAll(lastLinks)
                    .build()
        );
    }
}
