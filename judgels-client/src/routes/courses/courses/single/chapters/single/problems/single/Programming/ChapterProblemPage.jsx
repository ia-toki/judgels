import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';
import ChapterProblemStatementRoutes from './ChapterProblemStatementRoutes';

import './ChapterProblemPage.scss';

export default function ChapterProblemPage({ worksheet }) {
  return (
    <div className="chapter-programming-problem-page">
      <ChapterProblemStatementPage worksheet={worksheet} />
      <ChapterProblemStatementRoutes worksheet={worksheet} />
    </div>
  );
}
