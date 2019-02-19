import { stringify } from 'query-string';

import { get, put } from 'modules/api/http';
import { ProblemInfo, ProblemWorksheet } from 'modules/api/sandalphon/problem';

import { baseContestURL } from './contest';

export enum ContestProblemStatus {
  Open = 'OPEN',
  Closed = 'CLOSED',
}

export interface ContestProblem {
  alias: string;
  problemJid: string;
  status: ContestProblemStatus;
  submissionsLimit?: number;
  points?: number;
}

export interface ContestProblemData {
  alias: string;
  slug: string;
  status: ContestProblemStatus;
  submissionsLimit?: number;
  points?: number;
}

export interface ContestProblemsResponse {
  data: ContestProblem[];
  problemsMap: { [problemJid: string]: ProblemInfo };
  pointsMap?: { [problemJid: string]: number };
  totalSubmissionsMap: { [problemJid: string]: number };
  config: ContestProblemConfig;
}

export interface ContestProblemConfig {
  canManage: boolean;
}

export interface ContestProblemWorksheet {
  defaultLanguage: string;
  languages: string[];
  problem: ContestProblem;
  totalSubmissions: number;
  worksheet: ProblemWorksheet;
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/problems`;

export const contestProblemAPI = {
  getProblems: (token: string, contestJid: string): Promise<ContestProblemsResponse> => {
    return get(baseURL(contestJid), token);
  },

  setProblems: (token: string, contestJid: string, data: ContestProblemData[]): Promise<void> => {
    return put(baseURL(contestJid), token, data);
  },

  getProblemWorksheet: (
    token: string,
    contestJid: string,
    problemAlias: string,
    language?: string
  ): Promise<ContestProblemWorksheet> => {
    const params = stringify({ language });
    return get(`${baseURL(contestJid)}/${problemAlias}/programming/worksheet?${params}`, token);
  },
};
