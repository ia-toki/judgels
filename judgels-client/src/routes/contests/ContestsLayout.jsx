import { Outlet } from '@tanstack/react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

function ContestsLayout() {
  return <Outlet />;
}

export default withBreadcrumb('Contests')(ContestsLayout);
