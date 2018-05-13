import { APP_CONFIG } from '../../../conf';
import { get } from '../http';

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

export function createContestProblemAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getMyProblems: (token: string, contestJid: string): Promise<ContestContestantProblemsResponse> => {
      return get(`${baseURL}/${contestJid}/problems/mine`, token);
    },
  };
}
