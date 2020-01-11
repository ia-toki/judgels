package org.iatoki.judgels.jerahmeel.problemset;

import judgels.jerahmeel.persistence.ProblemSetModel;
import org.iatoki.judgels.jerahmeel.archive.Archive;

public final class ProblemSetServiceUtils {

    private ProblemSetServiceUtils() {
        // prevent instantiation
    }

    static ProblemSet createProblemSetFromModelAndArchive(ProblemSetModel problemSetModel, Archive archive) {
        return new ProblemSet(problemSetModel.id, problemSetModel.jid, problemSetModel.slug, archive, problemSetModel.name, problemSetModel.description);
    }
}
