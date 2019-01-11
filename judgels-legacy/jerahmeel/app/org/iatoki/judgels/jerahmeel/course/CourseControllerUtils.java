package org.iatoki.judgels.jerahmeel.course;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import play.i18n.Messages;

public final class CourseControllerUtils {

    private CourseControllerUtils() {
        // prevent instantiation
    }

    public static void appendTabLayout(LazyHtml content, Course course) {
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
                    new InternalLink(Messages.get("course.update"), routes.CourseController.editCourseGeneral(course.getId())),
                    new InternalLink(Messages.get("course.chapters"), routes.CourseController.jumpToChapters(course.getId()))
              ), c)
        );
        content.appendLayout(c -> headingLayout.render(Messages.get("course.course") + " #" + course.getId() + ": " + course.getName(), c));
    }

    public static ImmutableList.Builder<InternalLink> getBreadcrumbsBuilder() {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = ImmutableList.builder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("course.courses"), routes.CourseController.viewCourses()));

        return breadcrumbsBuilder;
    }
}
