package org.iatoki.judgels.jerahmeel.course;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.course.html.createCourseView;
import org.iatoki.judgels.jerahmeel.course.html.editCourseGeneralView;
import org.iatoki.judgels.jerahmeel.course.html.listCoursesView;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class CourseController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String COURSE = "course";

    private final CourseService courseService;

    @Inject
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public Result viewCourses() {
        return listCourses(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listCourses(long page, String orderBy, String orderDir, String filterString) {
        Page<Course> pageOfCourses = courseService.getPageOfCourses(page, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listCoursesView.render(pageOfCourses, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("course.list"), new InternalLink(Messages.get("commons.create"), routes.CourseController.createCourse()), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content);
        appendTemplateLayout(content);

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    public Result jumpToChapters(long courseId) {
        return redirect(org.iatoki.judgels.jerahmeel.course.chapter.routes.CourseChapterController.viewChapters(courseId));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createCourse() {
        Form<CourseUpsertForm> courseUpsertForm = Form.form(CourseUpsertForm.class);

        return showCreateCourse(courseUpsertForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateCourse() {
        Form<CourseUpsertForm> courseUpsertForm = Form.form(CourseUpsertForm.class).bindFromRequest();

        if (formHasErrors(courseUpsertForm)) {
            return showCreateCourse(courseUpsertForm);
        }

        CourseUpsertForm courseUpsertData = courseUpsertForm.get();
        Course course = courseService.createCourse(courseUpsertData.name, courseUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.CREATE.construct(COURSE, course.getJid(), course.getName()));

        return redirect(routes.CourseController.viewCourses());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editCourseGeneral(long courseId) throws CourseNotFoundException {
        Course course = courseService.findCourseById(courseId);
        CourseUpsertForm courseUpsertData = new CourseUpsertForm();
        courseUpsertData.name = course.getName();
        courseUpsertData.description = course.getDescription();

        Form<CourseUpsertForm> courseUpsertForm = Form.form(CourseUpsertForm.class).fill(courseUpsertData);

        return showEditCourseGeneral(courseUpsertForm, course);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditCourseGeneral(long courseId) throws CourseNotFoundException {
        Course course = courseService.findCourseById(courseId);
        Form<CourseUpsertForm> courseUpsertForm = Form.form(CourseUpsertForm.class).bindFromRequest();

        if (formHasErrors(courseUpsertForm)) {
            return showEditCourseGeneral(courseUpsertForm, course);
        }

        CourseUpsertForm courseUpsertData = courseUpsertForm.get();
        courseService.updateCourse(course.getJid(), courseUpsertData.name, courseUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (!course.getName().equals(courseUpsertData.name)) {
            JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.RENAME.construct(COURSE, course.getJid(), course.getName(), courseUpsertData.name));
        }
        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT.construct(COURSE, course.getJid(), courseUpsertData.name));

        return redirect(routes.CourseController.viewCourses());
    }

    private Result showCreateCourse(Form<CourseUpsertForm> courseUpsertForm) {
        LazyHtml content = new LazyHtml(createCourseView.render(courseUpsertForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("course.create"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("course.create"), routes.CourseController.createCourse())
        );
        appendTemplateLayout(content, "Create");
        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditCourseGeneral(Form<CourseUpsertForm> courseUpsertForm, Course course) {
        LazyHtml content = new LazyHtml(editCourseGeneralView.render(courseUpsertForm, course.getId()));
        CourseControllerUtils.appendTabLayout(content, course);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("course.update"), routes.CourseController.editCourseGeneral(course.getId()))
        );
        appendTemplateLayout(content, "Update");
        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = CourseControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }

    private void appendTemplateLayout(LazyHtml content, String... lastTitles) {
        StringBuilder titleBuilder = new StringBuilder("Courses");
        for (String lastTitle : lastTitles) {
            titleBuilder.append(" - ");
            titleBuilder.append(lastTitle);
        }

        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, titleBuilder.toString());
    }
}
