import { Outlet } from '@tanstack/react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';

function SubmissionsLayout() {
  return (
    <FullPageLayout>
      <Outlet />
    </FullPageLayout>
  );
}

export default withBreadcrumb('Submissions')(SubmissionsLayout);
