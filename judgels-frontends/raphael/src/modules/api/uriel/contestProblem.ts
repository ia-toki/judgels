import { stringify } from 'query-string';

import { get } from 'modules/api/http';
import { ProblemInfo, ProblemWorksheet } from 'modules/api/sandalphon/problem';

import { baseContestURL } from './contest';

export enum ContestProblemStatus {
  Open = 'OPEN',
  Closed = 'CLOSED',
}

export interface ContestProblem {
  problemJid: string;
  alias: string;
  status: ContestProblemStatus;
  submissionsLimit: number;
}

export interface ContestProblemsResponse {
  data: ContestProblem[];
  problemsMap: { [problemJid: string]: ProblemInfo };
  totalSubmissionsMap: { [problemJid: string]: number };
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

  getProblemWorksheet: (
    token: string,
    contestJid: string,
    problemAlias: string,
    language?: string
  ): Promise<ContestProblemWorksheet> => {
    const params = stringify({ language });
    return get(`${baseURL(contestJid)}/${problemAlias}/worksheet?${params}`, token);
  },
};
