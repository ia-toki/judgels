package org.iatoki.judgels.jerahmeel.training;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.InternalLink;
import play.i18n.Messages;

public final class TrainingControllerUtils {

    private TrainingControllerUtils() {
        // prevent instantiation
    }

    public static ImmutableList.Builder<InternalLink> getBreadcrumbsBuilder() {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = ImmutableList.builder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("training.home"), routes.TrainingController.index()));

        return breadcrumbsBuilder;
    }
}
