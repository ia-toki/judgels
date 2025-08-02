import { stringify } from 'query-string';

import { get, post } from '../http';

export const ProblemSetErrors = {
  SlugAlreadyExists: 'Jerahmeel:ProblemSetSlugAlreadyExists',
  ArchiveSlugNotFound: 'Jerahmeel:ArchiveSlugNotFound',
  ContestSlugsNotAllowed: 'Jerahmeel:ContestSlugsNotAllowed',
};

export const baseProblemSetsURL = `/api/v2/problemsets`;

export function baseProblemSetURL(problemSetJid) {
  return `${baseProblemSetsURL}/${problemSetJid}`;
}

export const problemSetAPI = {
  createProblemSet: (token, data) => {
    return post(baseProblemSetsURL, token, data);
  },

  updateProblemSet: (token, problemSetJid, data) => {
    return post(`${baseProblemSetURL(problemSetJid)}`, token, data);
  },

  getProblemSets: (token, archiveSlug, name, page) => {
    const params = stringify({ archiveSlug, name, page });
    return get(`${baseProblemSetsURL}?${params}`, token);
  },

  getProblemSetBySlug: problemSetSlug => {
    return get(`${baseProblemSetsURL}/slug/${problemSetSlug}`);
  },

  searchProblemSet: contestJid => {
    const params = stringify({ contestJid });
    return get(`${baseProblemSetsURL}/search?${params}`);
  },
};
