import { APP_CONFIG } from '../../../conf';
import { get } from '../http';
import { ProblemInfo, ProblemWorksheet } from '../sandalphon/problem';

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

export function createContestProblemAPI() {
  const baseURL = `${APP_CONFIG.apiUrls.uriel}/contests`;

  return {
    getMyProblems: (token: string, contestJid: string): Promise<ContestContestantProblemsResponse> => {
      return get(`${baseURL}/${contestJid}/problems/mine`, token);
    },

    getProblemWorksheet: (
      token: string,
      contestJid: string,
      problemAlias: string,
      language: string
    ): Promise<ContestContestantProblemWorksheet> => {
      const languageParam = language ? `?language=${language}` : '';
      return get(`${baseURL}/${contestJid}/problems/${problemAlias}/worksheet${languageParam}`, token);
    },
  };
}
