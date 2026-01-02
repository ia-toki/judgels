import { Outlet } from '@tanstack/react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

function ProblemSubmissionLayout() {
  return (
    <div>
      <Outlet />
    </div>
  );
}

export default withBreadcrumb('Submissions')(ProblemSubmissionLayout);
