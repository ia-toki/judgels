import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import UserRoute from '../../../components/UserRoute/UserRoute';

export default function AccountLayout() {
  const sidebarItems = [
    {
      path: 'info',
      title: 'Info',
    },
    {
      path: 'avatar',
      title: 'Change avatar',
    },
    {
      path: 'password',
      title: 'Reset password',
    },
  ];

  const contentWithSidebarProps = {
    title: 'My account',
    items: sidebarItems,
    smallContent: true,
    basePath: '/account',
  };

  return (
    <UserRoute>
      <FullPageLayout>
        <ScrollToTopOnMount />
        <ContentWithSidebar {...contentWithSidebarProps}>
          <Outlet />
        </ContentWithSidebar>
      </FullPageLayout>
    </UserRoute>
  );
}
