import { People, Shield } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';

export default function AdminLayout() {
  const sidebarItems = [
    {
      path: 'users',
      titleIcon: <People />,
      title: 'Users',
    },
    {
      path: 'roles',
      titleIcon: <Shield />,
      title: 'Roles',
    },
  ];

  const contentWithSidebarProps = {
    title: 'Admin',
    items: sidebarItems,
    basePath: '/admin',
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}
