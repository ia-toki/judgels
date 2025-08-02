import { stringify } from 'query-string';

import { get } from '../http';

export const baseProblemsURL = `/api/v2/problems`;

export const problemAPI = {
  getProblems: (token, tags, page) => {
    const params = stringify({ tags, page });
    return get(`${baseProblemsURL}?${params}`, token);
  },

  getProblemTags: () => {
    return get(`${baseProblemsURL}/tags`);
  },
};
