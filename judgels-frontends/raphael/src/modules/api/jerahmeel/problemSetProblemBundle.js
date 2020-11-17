import { ProblemSetProblemWorksheet as BaseProblemSetProblemProblemWorksheet } from './problemSetProblem';
import { ProblemWorksheet } from '../sandalphon/problemBundle';

export interface ProblemSetProblemWorksheet extends BaseProblemSetProblemProblemWorksheet {
  worksheet: ProblemWorksheet;
}
