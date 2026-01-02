import { Outlet } from '@tanstack/react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

function ProblemResultsLayout() {
  return <Outlet />;
}

export default withBreadcrumb('Results')(ProblemResultsLayout);
