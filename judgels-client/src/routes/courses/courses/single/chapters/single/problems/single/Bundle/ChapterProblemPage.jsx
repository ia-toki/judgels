import { useLocation } from '@tanstack/react-router';

import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';
import ChapterProblemSubmissionsPage from './submissions/ChapterProblemSubmissionsPage/ChapterProblemSubmissionsPage';

import './ChapterProblemPage.scss';

export default function ChapterProblemPage({ worksheet, renderNavigation }) {
  const location = useLocation();
  const isInSubmissionsPath = location.pathname.includes('/submissions');

  return (
    <div className="chapter-bundle-problem-page">
      <ChapterProblemStatementPage worksheet={worksheet} />
      {isInSubmissionsPath && (
        <ChapterProblemSubmissionsPage worksheet={worksheet} renderNavigation={renderNavigation} />
      )}
    </div>
  );
}
