import { ContestProblem } from './contestProblem';
import { ProblemWorksheet } from '../sandalphon/problemBundle';

export interface ContestProblemWorksheet {
  defaultLanguage: string;
  languages: string[];
  problem: ContestProblem;
  totalSubmissions: number;
  worksheet: ProblemWorksheet;
}
