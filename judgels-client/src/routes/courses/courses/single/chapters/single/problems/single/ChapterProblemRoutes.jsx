import { useOutletContext } from 'react-router';

import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import BundleChapterProblemSubmissionsPage from './Bundle/submissions/ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage';
import ChapterProblemWorkspacePage from './Programming/ChapterProblemWorkspacePage/ChapterProblemWorkspacePage';
import ProgrammingChapterProblemSubmissionsPage from './Programming/submissions/ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage';
import ChapterProblemSubmissionPage from './Programming/submissions/single/ChapterProblemSubmissionPage/ChapterProblemSubmissionPage';

function ChapterProblemIndexPage() {
  const { worksheet } = useOutletContext();
  const { problem } = worksheet;

  if (problem.type === ProblemType.Programming) {
    return <ChapterProblemWorkspacePage />;
  }
  return null;
}

function ChapterProblemSubmissionsPage() {
  const { worksheet, renderNavigation } = useOutletContext();
  const { problem } = worksheet;

  if (problem.type === ProblemType.Programming) {
    return <ProgrammingChapterProblemSubmissionsPage />;
  }
  return <BundleChapterProblemSubmissionsPage worksheet={worksheet} renderNavigation={renderNavigation} />;
}

export const chapterProblemRoutes = [
  {
    index: true,
    element: <ChapterProblemIndexPage />,
  },
  {
    path: 'submissions',
    element: <ChapterProblemSubmissionsPage />,
  },
  {
    path: 'submissions/all',
    element: <ProgrammingChapterProblemSubmissionsPage />,
  },
  {
    path: 'submissions/:submissionId',
    element: <ChapterProblemSubmissionPage />,
  },
];
