import { chapterLessonAPI } from '../../../../../../../../modules/api/jerahmeel/chapterLesson';
import { chapterProblemAPI } from '../../../../../../../../modules/api/jerahmeel/chapterProblem';
import { getToken } from '../../../../../../../../modules/session';

export async function getResources(chapterJid) {
  const token = getToken();
  return await Promise.all([
    chapterLessonAPI.getLessons(token, chapterJid),
    chapterProblemAPI.getProblems(token, chapterJid),
  ]);
}
