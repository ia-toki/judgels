import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import UserRoute from '../../../components/UserRoute/UserRoute';
import { isUserRegistrationEnabled, isUserResetPasswordEnabled } from '../../../modules/webConfig';

export default function AccountLayout() {
  const sidebarItems = [
    {
      path: 'info',
      title: 'Info',
      visible: isUserRegistrationEnabled(),
    },
    {
      path: 'avatar',
      title: 'Change avatar',
      visible: isUserRegistrationEnabled(),
    },
    {
      path: 'password',
      title: 'Reset password',
      visible: isUserResetPasswordEnabled(),
    },
  ].filter(item => item.visible);

  const contentWithSidebarProps = {
    title: 'My account',
    items: sidebarItems,
    smallContent: true,
    basePath: '/account',
  };

  return (
    <UserRoute>
      <FullPageLayout>
        <ContentWithSidebar {...contentWithSidebarProps}>
          <Outlet />
        </ContentWithSidebar>
      </FullPageLayout>
    </UserRoute>
  );
}
