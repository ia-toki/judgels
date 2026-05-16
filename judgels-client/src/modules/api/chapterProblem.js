import { stringify } from 'query-string';

import { baseChapterURL } from './chapter';
import { get, put } from './http';

const baseURL = chapterJid => `${baseChapterURL(chapterJid)}/problems`;

export const chapterProblemAPI = {
  setProblems: (token, chapterJid, data) => {
    return put(baseURL(chapterJid), token, data);
  },

  getProblems: (token, chapterJid) => {
    return get(baseURL(chapterJid), token);
  },

  getProblemWorksheet: (token, chapterJid, problemAlias, language) => {
    const params = stringify({ language });
    return get(`${baseURL(chapterJid)}/${problemAlias}/worksheet?${params}`, token);
  },
};
