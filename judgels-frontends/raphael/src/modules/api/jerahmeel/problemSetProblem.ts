import { stringify } from 'query-string';

import { get, put } from '../../../modules/api/http';
import { Profile } from '../jophiel/profile';
import { ProblemInfo, ProblemType } from '../sandalphon/problem';
import { baseProblemSetURL } from './problemSet';
import { ProblemProgress, ProblemStats, ProblemTopStats } from './problem';

export interface ProblemSetProblem {
  alias: string;
  problemJid: string;
  type: ProblemType;
}

export interface ProblemSetProblemData {
  alias: string;
  slug: string;
  type: ProblemType;
}

export interface ProblemSetProblemsResponse {
  data: ProblemSetProblem[];
  problemsMap: { [problemJid: string]: ProblemInfo };
  problemProgressesMap: { [problemJid: string]: ProblemProgress };
  problemStatsMap: { [problemJid: string]: ProblemStats };
}

export interface ProblemSetProblemWorksheet {
  defaultLanguage: string;
  languages: string[];
  problem: ProblemSetProblem;
}

export interface ProblemStatsResponse {
  progress: ProblemProgress;
  stats: ProblemStats;
  topStats: ProblemTopStats;
  profilesMap: { [userJid: string]: Profile };
}

const baseURL = (problemSetJid: string) => `${baseProblemSetURL(problemSetJid)}/problems`;

export const problemSetProblemAPI = {
  setProblems: (token: string, problemSetJid: string, data: ProblemSetProblemData[]): Promise<void> => {
    return put(baseURL(problemSetJid), token, data);
  },

  getProblems: (token: string, problemSetJid: string): Promise<ProblemSetProblemsResponse> => {
    return get(baseURL(problemSetJid), token);
  },

  getProblem: (token: string, problemSetJid: string, problemAlias: string): Promise<ProblemSetProblem> => {
    return get(`${baseURL(problemSetJid)}/${problemAlias}`, token);
  },

  getProblemWorksheet: (
    token: string,
    problemSetJid: string,
    problemAlias: string,
    language?: string
  ): Promise<ProblemSetProblemWorksheet> => {
    const params = stringify({ language });
    return get(`${baseURL(problemSetJid)}/${problemAlias}/worksheet?${params}`, token);
  },

  getProblemStats: (token: string, problemSetJid: string, problemAlias: string): Promise<ProblemStatsResponse> => {
    return get(`${baseURL(problemSetJid)}/${problemAlias}/stats`, token);
  },
};
