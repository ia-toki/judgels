import { Route, Routes } from 'react-router-dom';

import ContentWithTopbar from '../../../../../../../../../components/ContentWithTopbar/ContentWithTopbar';
import ChapterProblemWorkspacePage from './ChapterProblemWorkspacePage/ChapterProblemWorkspacePage';
import ChapterProblemSubmissionRoutes from './submissions/ChapterProblemSubmissionRoutes';

import './ChapterProblemStatementRoutes.scss';

export default function ChapterProblemStatementRoutes({ worksheet, renderNavigation }) {
  const topbarItems = [
    {
      path: '',
      title: 'Code',
    },
    {
      path: 'submissions',
      title: 'Submissions',
    },
  ];

  return (
    <ContentWithTopbar className="chapter-problem-statement-routes" items={topbarItems}>
      <Routes>
        <Route
          index
          element={<ChapterProblemWorkspacePage worksheet={worksheet} renderNavigation={renderNavigation} />}
        />
        <Route
          path="submissions/*"
          element={<ChapterProblemSubmissionRoutes worksheet={worksheet} renderNavigation={renderNavigation} />}
        />
      </Routes>
    </ContentWithTopbar>
  );
}
