import { stringify } from 'query-string';

import { get } from '../../../modules/api/http';

import { ProblemInfo, ProblemType } from '../sandalphon/problem';
import { baseProblemSetURL } from './problemSet';
import { ProblemProgress, ProblemStats } from './problem';

export interface ProblemSetProblem {
  alias: string;
  problemJid: string;
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

const baseURL = (problemSetJid: string) => `${baseProblemSetURL(problemSetJid)}/problems`;

export const problemSetProblemAPI = {
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
};
