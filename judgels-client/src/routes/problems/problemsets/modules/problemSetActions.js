import { problemSetAPI } from '../../../../modules/api/jerahmeel/problemSet';
import { getToken } from '../../../../modules/session';

export async function getProblemSets(archiveSlug, name, page) {
  const token = getToken();
  return await problemSetAPI.getProblemSets(token, archiveSlug, name, page);
}
