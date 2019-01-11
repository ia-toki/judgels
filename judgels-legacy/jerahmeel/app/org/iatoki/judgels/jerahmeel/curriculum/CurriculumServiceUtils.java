package org.iatoki.judgels.jerahmeel.curriculum;

final class CurriculumServiceUtils {

    private CurriculumServiceUtils() {
        // prevent instantiation
    }

    static Curriculum createCurriculumFromModel(CurriculumModel curriculumModel) {
        return new Curriculum(curriculumModel.id, curriculumModel.jid, curriculumModel.name, curriculumModel.description);
    }
}
