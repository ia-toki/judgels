import { Outlet } from '@tanstack/react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import './ProblemsLayout.scss';

function ProblemsLayout() {
  return <Outlet />;
}

export default withBreadcrumb('Problems')(ProblemsLayout);
