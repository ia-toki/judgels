import { useLocation } from '@tanstack/react-router';

import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';
import ChapterProblemSubmissionsPage from './submissions/ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage';

export default function ChapterProblemPage({ worksheet, renderNavigation }) {
  const location = useLocation();
  const isInSubmissionsPath = location.pathname.includes('/submissions');

  return (
    <div className="chapter-problem-panes chapter-problem-panes--centered">
      <ChapterProblemStatementPage worksheet={worksheet} />
      {isInSubmissionsPath && (
        <ChapterProblemSubmissionsPage worksheet={worksheet} renderNavigation={renderNavigation} />
      )}
    </div>
  );
}
