import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

export const baseProblemsURL = `${APP_CONFIG.apiUrl}/problems`;

export const problemAPI = {
  getProblems: (token, tags, page) => {
    const params = stringify({ tags, page });
    return get(`${baseProblemsURL}?${params}`, token);
  },

  getProblemTags: () => {
    return get(`${baseProblemsURL}/tags`);
  },
};
