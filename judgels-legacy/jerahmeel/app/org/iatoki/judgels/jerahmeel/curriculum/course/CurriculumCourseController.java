package org.iatoki.judgels.jerahmeel.curriculum.course;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumControllerUtils;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumService;
import org.iatoki.judgels.jerahmeel.curriculum.course.html.editCurriculumCourseView;
import org.iatoki.judgels.jerahmeel.curriculum.course.html.listAddCurriculumCoursesView;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class CurriculumCourseController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String COURSE = "course";
    private static final String CURRICULUM = "curriculum";

    private final CourseService courseService;
    private final CurriculumCourseService curriculumCourseService;
    private final CurriculumService curriculumService;

    @Inject
    public CurriculumCourseController(CourseService courseService, CurriculumCourseService curriculumCourseService, CurriculumService curriculumService) {
        this.courseService = courseService;
        this.curriculumCourseService = curriculumCourseService;
        this.curriculumService = curriculumService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewCourses(long curriculumId) throws CurriculumNotFoundException {
        return listAddCourses(curriculumId, 0, "alias", "asc", "");
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listAddCourses(long curriculumId, long page, String orderBy, String orderDir, String filterString) throws CurriculumNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);

        Page<CurriculumCourse> pageOfCurriculumCourses = curriculumCourseService.getPageOfCurriculumCourses(curriculum.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
        List<String> courseJids = pageOfCurriculumCourses.getData().stream().map(e -> e.getCourseJid()).collect(Collectors.toList());
        Map<String, Course> coursesMap = courseService.getCoursesMapByJids(courseJids);

        Form<CurriculumCourseAddForm> curriculumCourseAddForm = Form.form(CurriculumCourseAddForm.class);

        return showListAddCourses(curriculum, curriculumCourseAddForm, pageOfCurriculumCourses, coursesMap, orderBy, orderDir, filterString);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddCourse(long curriculumId, long page, String orderBy, String orderDir, String filterString) throws CurriculumNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        Form<CurriculumCourseAddForm> curriculumCourseCreateForm = Form.form(CurriculumCourseAddForm.class).bindFromRequest();

        if (formHasErrors(curriculumCourseCreateForm)) {
            Page<CurriculumCourse> pageOfCurriculumCourses = curriculumCourseService.getPageOfCurriculumCourses(curriculum.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> courseJids = pageOfCurriculumCourses.getData().stream().map(e -> e.getCourseJid()).collect(Collectors.toList());
            Map<String, Course> coursesMap = courseService.getCoursesMapByJids(courseJids);

            return showListAddCourses(curriculum, curriculumCourseCreateForm, pageOfCurriculumCourses, coursesMap, orderBy, orderDir, filterString);
        }

        CurriculumCourseAddForm curriculumCourseCreateData = curriculumCourseCreateForm.get();
        if (!courseService.courseExistsByJid(curriculumCourseCreateData.courseJid)) {
            curriculumCourseCreateForm.reject(Messages.get("error.curriculum.invalidJid"));
            Page<CurriculumCourse> pageOfCurriculumCourses = curriculumCourseService.getPageOfCurriculumCourses(curriculum.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> courseJids = pageOfCurriculumCourses.getData().stream().map(e -> e.getCourseJid()).collect(Collectors.toList());
            Map<String, Course> coursesMap = courseService.getCoursesMapByJids(courseJids);

            return showListAddCourses(curriculum, curriculumCourseCreateForm, pageOfCurriculumCourses, coursesMap, orderBy, orderDir, filterString);
        }

        if (curriculumCourseService.existsByCurriculumJidAndAlias(curriculum.getJid(), curriculumCourseCreateData.alias)) {
            curriculumCourseCreateForm.reject(Messages.get("error.curriculum.course.duplicateAlias"));
            Page<CurriculumCourse> pageOfCurriculumCourses = curriculumCourseService.getPageOfCurriculumCourses(curriculum.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> courseJids = pageOfCurriculumCourses.getData().stream().map(e -> e.getCourseJid()).collect(Collectors.toList());
            Map<String, Course> coursesMap = courseService.getCoursesMapByJids(courseJids);

            return showListAddCourses(curriculum, curriculumCourseCreateForm, pageOfCurriculumCourses, coursesMap, orderBy, orderDir, filterString);
        }

        if (curriculumCourseService.existsByCurriculumJidAndCourseJid(curriculum.getJid(), curriculumCourseCreateData.courseJid)) {
            curriculumCourseCreateForm.reject(Messages.get("error.curriculum.courseExist"));
            Page<CurriculumCourse> pageOfCurriculumCourses = curriculumCourseService.getPageOfCurriculumCourses(curriculum.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> courseJids = pageOfCurriculumCourses.getData().stream().map(e -> e.getCourseJid()).collect(Collectors.toList());
            Map<String, Course> coursesMap = courseService.getCoursesMapByJids(courseJids);

            return showListAddCourses(curriculum, curriculumCourseCreateForm, pageOfCurriculumCourses, coursesMap, orderBy, orderDir, filterString);
        }

        CurriculumCourse curriculumCourse = curriculumCourseService.addCurriculumCourse(curriculum.getJid(), curriculumCourseCreateData.courseJid, curriculumCourseCreateData.alias, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(CURRICULUM, curriculum.getJid(), curriculum.getName(), COURSE, curriculumCourse.getCourseJid(), course.getName()));

        return redirect(routes.CurriculumCourseController.viewCourses(curriculum.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editCourse(long curriculumId, long curriculumCourseId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);

        if (!curriculumCourse.getCurriculumJid().equals(curriculum.getJid())) {
            return notFound();
        }

        CurriculumCourseEditForm curriculumCourseEditData = new CurriculumCourseEditForm();
        curriculumCourseEditData.alias = curriculumCourse.getAlias();

        Form<CurriculumCourseEditForm> curriculumCourseEditForm = Form.form(CurriculumCourseEditForm.class).fill(curriculumCourseEditData);

        return showEditCourse(curriculum, curriculumCourse, curriculumCourseEditForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditCourse(long curriculumId, long curriculumCourseId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);

        if (!curriculumCourse.getCurriculumJid().equals(curriculum.getJid())) {
            return notFound();
        }

        Form<CurriculumCourseEditForm> curriculumCourseEditForm = Form.form(CurriculumCourseEditForm.class).bindFromRequest();
        if (formHasErrors(curriculumCourseEditForm)) {
            return showEditCourse(curriculum, curriculumCourse, curriculumCourseEditForm);
        }

        CurriculumCourseEditForm curriculumCourseEditData = curriculumCourseEditForm.get();
        if (!curriculumCourseEditData.alias.equals(curriculumCourse.getAlias()) && curriculumCourseService.existsByCurriculumJidAndAlias(curriculum.getJid(), curriculumCourseEditData.alias)) {
            curriculumCourseEditForm.reject(Messages.get("error.curriculum.course.duplicateAlias"));

            return showEditCourse(curriculum, curriculumCourse, curriculumCourseEditForm);
        }

        curriculumCourseService.updateCurriculumCourse(curriculumCourse.getId(), curriculumCourseEditData.alias, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT_IN.construct(CURRICULUM, curriculum.getJid(), curriculum.getName(), COURSE, curriculumCourse.getCourseJid(), course.getName()));

        return redirect(routes.CurriculumCourseController.viewCourses(curriculum.getId()));
    }

    @Transactional
    public Result removeCourse(long curriculumId, long curriculumCourseId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid())) {
            return forbidden();
        }

        curriculumCourseService.removeCurriculumCourse(curriculumCourseId);

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.REMOVE_FROM.construct(CURRICULUM, curriculum.getJid(), curriculum.getName(), COURSE, curriculumCourse.getCourseJid(), course.getName()));

        return redirect(routes.CurriculumCourseController.viewCourses(curriculum.getId()));
    }

    private Result showListAddCourses(Curriculum curriculum, Form<CurriculumCourseAddForm> curriculumCourseAddForm, Page<CurriculumCourse> pageOfCurriculumCourses, Map<String, Course> coursesMap, String orderBy, String orderDir, String filterString) {
        LazyHtml content = new LazyHtml(listAddCurriculumCoursesView.render(curriculum.getId(), pageOfCurriculumCourses, coursesMap, orderBy, orderDir, filterString, curriculumCourseAddForm));
        CurriculumControllerUtils.appendTabLayout(content, curriculum);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum);
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Curriculums");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditCourse(Curriculum curriculum, CurriculumCourse curriculumCourse, Form<CurriculumCourseEditForm> curriculumCourseEditForm) {
        LazyHtml content = new LazyHtml(editCurriculumCourseView.render(curriculumCourseEditForm, curriculum.getId(), curriculumCourse.getId()));
        CurriculumControllerUtils.appendTabLayout(content, curriculum);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum,
                new InternalLink(Messages.get("commons.update"), routes.CurriculumCourseController.editCourse(curriculum.getId(), curriculumCourse.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Curriculums");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Curriculum curriculum, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = CurriculumControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("curriculum.courses"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.jumpToCourses(curriculum.getId())));
        breadcrumbsBuilder.add(new InternalLink(Messages.get("commons.view"), routes.CurriculumCourseController.viewCourses(curriculum.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
