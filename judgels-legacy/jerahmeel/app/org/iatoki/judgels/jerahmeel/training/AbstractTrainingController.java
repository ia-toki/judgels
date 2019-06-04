package org.iatoki.judgels.jerahmeel.training;

import org.iatoki.judgels.jerahmeel.AbstractJerahmeelController;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.i18n.Messages;
import play.mvc.Result;

public abstract class AbstractTrainingController extends AbstractJerahmeelController {
    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation(Messages.get("training.home"), routes.TrainingController.index());

        return super.renderTemplate(template);
    }
}
