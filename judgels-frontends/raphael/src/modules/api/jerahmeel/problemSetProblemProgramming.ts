import { ProblemSetProblemWorksheet as BaseProblemSetProblemProblemWorksheet } from './problemSetProblem';
import { ProblemWorksheet } from '../sandalphon/problemProgramming';

export interface ProblemSetProblemWorksheet extends BaseProblemSetProblemProblemWorksheet {
  worksheet: ProblemWorksheet;
}
