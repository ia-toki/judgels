import { archiveAPI } from '../../../../modules/api/jerahmeel/archive';
import { getToken } from '../../../../modules/session';

export async function getArchives() {
  const token = getToken();
  return await archiveAPI.getArchives(token);
}
