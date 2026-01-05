import { Outlet } from '@tanstack/react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';

export default function SubmissionsLayout() {
  return (
    <FullPageLayout>
      <Outlet />
    </FullPageLayout>
  );
}
