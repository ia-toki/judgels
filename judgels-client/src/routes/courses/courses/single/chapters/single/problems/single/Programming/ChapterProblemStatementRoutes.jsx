import { Route } from 'react-router';

import ContentWithTopbar from '../../../../../../../../../components/ContentWithTopbar/ContentWithTopbar';
import ChapterProblemWorkspacePage from './ChapterProblemWorkspacePage/ChapterProblemWorkspacePage';
import ChapterProblemSubmissionRoutes from './submissions/ChapterProblemSubmissionRoutes';

import './ChapterProblemStatementRoutes.scss';

export default function ChapterProblemStatementRoutes({ worksheet, renderNavigation }) {
  const topbarItems = [
    {
      id: '@',
      title: 'Submit',
      routeComponent: Route,
      render: props => (
        <ChapterProblemWorkspacePage {...props} worksheet={worksheet} renderNavigation={renderNavigation} />
      ),
    },
    {
      id: 'submissions',
      title: 'Submissions',
      routeComponent: Route,
      component: ChapterProblemSubmissionRoutes,
    },
  ];

  return <ContentWithTopbar className="chapter-problem-statement-routes" items={topbarItems} />;
}
