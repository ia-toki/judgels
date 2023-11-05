import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';
import ChapterProblemStatementRoutes from './ChapterProblemStatementRoutes';

import './ChapterProblemPage.scss';

export default function ChapterProblemPage({ worksheet, renderNavigation }) {
  return (
    <div className="chapter-programming-problem-page">
      <ChapterProblemStatementPage worksheet={worksheet} renderNavigation={renderNavigation} />
      <ChapterProblemStatementRoutes worksheet={worksheet} />
    </div>
  );
}
