package org.iatoki.judgels.jerahmeel.curriculum;

import org.iatoki.judgels.jerahmeel.AbstractJerahmeelController;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractCurriculumController extends AbstractJerahmeelController {
    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation(Messages.get("curriculum.curriculums"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.viewCurriculums());

        return super.renderTemplate(template);
    }

    protected void appendTabs(HtmlTemplate template, Curriculum curriculum) {
        template.addMainTab(Messages.get("curriculum.update"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.editCurriculumGeneral(curriculum.getId()));
        template.addMainTab(Messages.get("curriculum.courses"), org.iatoki.judgels.jerahmeel.curriculum.routes.CurriculumController.jumpToCourses(curriculum.getId()));
        template.setMainTitle(Messages.get("curriculum.curriculum") + " #" + curriculum.getId() + ": " + curriculum.getName());
    }
}
