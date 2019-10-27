import { ChapterProblemWorksheet as BaseChapterProblemWorksheet } from './chapterProblem';
import { ProblemWorksheet } from '../sandalphon/problemBundle';

export interface ChapterProblemWorksheet extends BaseChapterProblemWorksheet {
  worksheet: ProblemWorksheet;
}
