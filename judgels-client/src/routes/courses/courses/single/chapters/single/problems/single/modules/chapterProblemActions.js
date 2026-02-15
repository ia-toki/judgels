import { chapterProblemAPI } from '../../../../../../../../../modules/api/jerahmeel/chapterProblem';
import { getToken } from '../../../../../../../../../modules/session';

export async function getProblemWorksheet(chapterJid, problemAlias, language) {
  const token = getToken();
  return await chapterProblemAPI.getProblemWorksheet(token, chapterJid, problemAlias, language);
}
