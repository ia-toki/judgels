import { ContestProblemData } from 'modules/api/uriel/contestProblem';

export interface ContestProblemProcessor {
  toString: (problems: ContestProblemData[]) => string;
  toContestProblemData: (problems: string) => ContestProblemData[];
}
