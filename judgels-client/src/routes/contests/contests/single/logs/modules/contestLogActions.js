import { contestLogAPI } from '../../../../../../modules/api/uriel/contestLog';
import { getToken } from '../../../../../../modules/session';

export async function getLogs(contestJid, username, problemAlias, page) {
  const token = getToken();
  return await contestLogAPI.getLogs(token, contestJid, username, problemAlias, page);
}
