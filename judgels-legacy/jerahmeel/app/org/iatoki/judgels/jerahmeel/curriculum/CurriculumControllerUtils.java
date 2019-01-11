package org.iatoki.judgels.jerahmeel.curriculum;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import play.i18n.Messages;

public final class CurriculumControllerUtils {

    private CurriculumControllerUtils() {
        // prevent instantiation
    }

    public static void appendTabLayout(LazyHtml content, Curriculum curriculum) {
        content.appendLayout(c -> tabLayout.render(ImmutableList.of(
                    new InternalLink(Messages.get("curriculum.update"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.editCurriculumGeneral(curriculum.getId())),
                    new InternalLink(Messages.get("curriculum.courses"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.jumpToCourses(curriculum.getId()))
              ), c)
        );
        content.appendLayout(c -> headingLayout.render(Messages.get("curriculum.curriculum") + " #" + curriculum.getId() + ": " + curriculum.getName(), c));
    }

    public static ImmutableList.Builder<InternalLink> getBreadcrumbsBuilder() {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = ImmutableList.builder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("curriculum.curriculums"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.viewCurriculums()));

        return breadcrumbsBuilder;
    }
}
