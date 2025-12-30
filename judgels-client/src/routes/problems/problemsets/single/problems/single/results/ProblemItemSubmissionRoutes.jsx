import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ProblemSubmissionSummaryPage from './ProblemSubmissionSummaryPage/ProblemSubmissionSummaryPage';
import ProblemSubmissionsPage from './ProblemSubmissionsPage/ProblemSubmissionsPage';

export const problemItemSubmissionRoutes = [
  {
    index: true,
    element: <ProblemSubmissionSummaryPage />,
  },
  {
    path: 'users/:username',
    element: <ProblemSubmissionSummaryPage />,
  },
  {
    path: 'all',
    element: <ProblemSubmissionsPage />,
  },
];

function ProblemItemSubmissionLayout() {
  return (
    <div>
      <Outlet />
    </div>
  );
}

export default withBreadcrumb('Results')(ProblemItemSubmissionLayout);
