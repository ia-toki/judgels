import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import BundleChapterProblemPage from './Bundle/ChapterProblemPage';
import { useChapterProblemContext } from './ChapterProblemContext';
import ProgrammingChapterProblemStatementLayout from './Programming/ChapterProblemStatementLayout';

export default function ChapterProblemStatementLayout() {
  const { worksheet, renderNavigation } = useChapterProblemContext();
  const problemType = worksheet?.problem?.type;

  if (problemType === ProblemType.Bundle) {
    return <BundleChapterProblemPage worksheet={worksheet} renderNavigation={renderNavigation} />;
  }

  return <ProgrammingChapterProblemStatementLayout worksheet={worksheet} />;
}
