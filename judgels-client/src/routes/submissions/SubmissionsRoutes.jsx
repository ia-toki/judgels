import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import SubmissionsPage from './SubmissionsPage/SubmissionsPage';
import SubmissionPage from './single/SubmissionPage/SubmissionPage';

function SubmissionsLayout() {
  return (
    <FullPageLayout>
      <Outlet />
    </FullPageLayout>
  );
}

const SubmissionsLayoutWithBreadcrumb = withBreadcrumb('Submissions')(SubmissionsLayout);

export const routes = [
  {
    path: 'submissions',
    element: <SubmissionsLayoutWithBreadcrumb />,
    children: [
      {
        index: true,
        element: <SubmissionsPage />,
      },
      {
        path: 'mine',
        element: <SubmissionsPage />,
      },
      {
        path: ':submissionId',
        element: <SubmissionPage />,
      },
    ],
  },
];
