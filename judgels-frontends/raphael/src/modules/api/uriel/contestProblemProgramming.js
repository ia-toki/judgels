import { ProblemWorksheet } from '../sandalphon/problemProgramming';
import { ContestProblem } from './contestProblem';

export interface ContestProblemWorksheet {
  defaultLanguage: string;
  languages: string[];
  problem: ContestProblem;
  totalSubmissions: number;
  worksheet: ProblemWorksheet;
}
