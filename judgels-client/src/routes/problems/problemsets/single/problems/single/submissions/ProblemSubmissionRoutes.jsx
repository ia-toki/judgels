import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ProblemSubmissionsPage from './ProblemSubmissionsPage/ProblemSubmissionsPage';
import ProblemSubmissionPage from './single/ProblemSubmissionPage/ProblemSubmissionPage';

export const problemSubmissionRoutes = [
  {
    index: true,
    element: <ProblemSubmissionsPage />,
  },
  {
    path: 'mine',
    element: <ProblemSubmissionsPage />,
  },
  {
    path: ':submissionId',
    element: <ProblemSubmissionPage />,
  },
];

function ProblemSubmissionLayout() {
  return (
    <div>
      <Outlet />
    </div>
  );
}

export default withBreadcrumb('Submissions')(ProblemSubmissionLayout);
