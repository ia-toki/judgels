import { contestEditorialAPI } from '../../../../../../modules/api/uriel/contestEditorial';

export async function getEditorial(contestJid, language) {
  return await contestEditorialAPI.getEditorial(contestJid, language);
}
