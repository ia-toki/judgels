import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';
import ChapterProblemSubmissionRoutes from './submissions/ChapterProblemSubmissionRoutes';

import './ChapterProblemPage.scss';

export default function ChapterProblemPage({ worksheet, renderNavigation }) {
  return (
    <div className="chapter-bundle-problem-page">
      <ChapterProblemStatementPage worksheet={worksheet} />
      <ChapterProblemSubmissionRoutes worksheet={worksheet} renderNavigation={renderNavigation} />
    </div>
  );
}
