import { contestEditorialAPI } from '../../../../../../modules/api/uriel/contestEditorial';

export function getEditorial(contestJid, language) {
  return async () => {
    return await contestEditorialAPI.getEditorial(contestJid, language);
  };
}
