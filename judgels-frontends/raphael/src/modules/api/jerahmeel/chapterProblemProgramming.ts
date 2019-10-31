import { ChapterProblemWorksheet as BaseChapterProblemWorksheet } from './chapterProblem';
import { ProblemWorksheet } from '../sandalphon/problemProgramming';

export interface ChapterProblemWorksheet extends BaseChapterProblemWorksheet {
  worksheet: ProblemWorksheet;
}
