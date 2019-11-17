import { get } from '../../../modules/api/http';

import { ProblemInfo } from '../sandalphon/problem';
import { baseProblemSetURL } from './problemSet';

export interface ProblemSetProblem {
  alias: string;
  problemJid: string;
}

export interface ProblemSetProblemsResponse {
  data: ProblemSetProblem[];
  problemsMap: { [problemJid: string]: ProblemInfo };
}

const baseURL = (problemSetJid: string) => `${baseProblemSetURL(problemSetJid)}/problems`;

export const problemSetProblemAPI = {
  getProblems: (token: string, problemSetJid: string): Promise<ProblemSetProblemsResponse> => {
    return get(baseURL(problemSetJid), token);
  },
};
