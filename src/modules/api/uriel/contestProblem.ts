import { APP_CONFIG } from '../../../conf';
import { get } from '../http';
import { ProblemStatement } from '../sandalphon/problem';

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
  problemNamesMap: { [problemJid: string]: string };
}

export interface ContestContestantProblemStatement {
  problem: ContestProblem;
  totalSubmissions: number;
  statement: ProblemStatement;
}

export function createContestProblemAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getMyProblems: (token: string, contestJid: string): Promise<ContestContestantProblemsResponse> => {
      return get(`${baseURL}/${contestJid}/problems/mine`, token);
    },

    getProblemStatement: (
      token: string,
      contestJid: string,
      problemAlias: string
    ): Promise<ContestContestantProblemStatement> => {
      return get(`${baseURL}/${contestJid}/problems/${problemAlias}/statement`, token);
    },
  };
}
