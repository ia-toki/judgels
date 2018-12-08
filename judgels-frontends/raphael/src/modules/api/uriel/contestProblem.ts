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

export interface ContestContestantProblem {
  problem: ContestProblem;
  totalSubmissions: number;
}

export interface ContestContestantProblemsResponse {
  data: ContestContestantProblem[];
  problemsMap: { [problemJid: string]: ProblemInfo };
}

export interface ContestContestantProblemWorksheet {
  defaultLanguage: string;
  languages: string[];
  contestantProblem: ContestContestantProblem;
  worksheet: ProblemWorksheet;
}

const baseURL = (contestJid: string) => `${baseContestURL(contestJid)}/problems`;

export const contestProblemAPI = {
  getMyProblems: (token: string, contestJid: string): Promise<ContestContestantProblemsResponse> => {
    return get(`${baseURL(contestJid)}/mine`, token);
  },

  getProblemWorksheet: (
    token: string,
    contestJid: string,
    problemAlias: string,
    language?: string
  ): Promise<ContestContestantProblemWorksheet> => {
    const params = stringify({ language });
    return get(`${baseURL(contestJid)}/${problemAlias}/worksheet?${params}`, token);
  },
};
