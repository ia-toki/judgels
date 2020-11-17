import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';
import { Page } from '../pagination';

export interface ProblemSet {
  id: number;
  jid: string;
  archiveJid: string;
  slug: string;
  name: string;
  description: string;
}

export interface ProblemSetProgress {
  score: number;
  totalProblems: number;
}

export interface ProblemSetsResponse {
  data: Page<ProblemSet>;
  archiveSlugsMap: { [archiveJid: string]: string };
  archiveDescriptionsMap: { [archiveJid: string]: string };
  archiveName?: string;
  problemSetProgressesMap: { [problemSetJid: string]: ProblemSetProgress };
}

export interface ProblemSetCreateData {
  slug: string;
  name: string;
  archiveSlug: string;
  description?: string;
}

export interface ProblemSetUpdateData {
  slug?: string;
  name?: string;
  archiveSlug?: string;
  description?: string;
}

export enum ProblemSetErrors {
  SlugAlreadyExists = 'Jerahmeel:ProblemSetSlugAlreadyExists',
  ArchiveSlugNotFound = 'Jerahmeel:ArchiveSlugNotFound',
}

export const baseProblemSetsURL = `${APP_CONFIG.apiUrls.jerahmeel}/problemsets`;

export function baseProblemSetURL(problemSetJid: string) {
  return `${baseProblemSetsURL}/${problemSetJid}`;
}

export const problemSetAPI = {
  createProblemSet: (token: string, data: ProblemSetCreateData): Promise<ProblemSet> => {
    return post(baseProblemSetsURL, token, data);
  },

  updateProblemSet: (token: string, problemSetJid: string, data: ProblemSetUpdateData): Promise<ProblemSet> => {
    return post(`${baseProblemSetURL(problemSetJid)}`, token, data);
  },

  getProblemSets: (token: string, archiveSlug?: string, name?: string, page?: number): Promise<ProblemSetsResponse> => {
    const params = stringify({ archiveSlug, name, page });
    return get(`${baseProblemSetsURL}?${params}`, token);
  },

  getProblemSetBySlug: (problemSetSlug: string): Promise<ProblemSet> => {
    return get(`${baseProblemSetsURL}/slug/${problemSetSlug}`);
  },
};
