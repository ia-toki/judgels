import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';
import ChapterProblemSubmissionRoutes from './submissions/ChapterProblemSubmissionRoutes';

import './ChapterProblemPage.scss';

export default function ChapterProblemPage({ worksheet }) {
  return (
    <div className="chapter-bundle-problem-page">
      <ChapterProblemStatementPage worksheet={worksheet} />
      <ChapterProblemSubmissionRoutes />
    </div>
  );
}
