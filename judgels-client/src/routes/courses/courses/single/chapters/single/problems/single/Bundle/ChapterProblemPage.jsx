import { Outlet } from 'react-router';

import ChapterProblemStatementPage from './ChapterProblemStatementPage/ChapterProblemStatementPage';

import './ChapterProblemPage.scss';

export default function ChapterProblemPage({ worksheet, renderNavigation }) {
  return (
    <div className="chapter-bundle-problem-page">
      <ChapterProblemStatementPage worksheet={worksheet} />
      <Outlet context={{ worksheet, renderNavigation }} />
    </div>
  );
}
