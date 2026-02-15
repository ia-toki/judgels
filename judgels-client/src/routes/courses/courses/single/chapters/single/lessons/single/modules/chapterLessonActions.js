import { chapterLessonAPI } from '../../../../../../../../../modules/api/jerahmeel/chapterLesson';
import { getToken } from '../../../../../../../../../modules/session';

export async function getLessonStatement(chapterJid, lessonAlias, language) {
  const token = getToken();
  return await chapterLessonAPI.getLessonStatement(token, chapterJid, lessonAlias, language);
}
