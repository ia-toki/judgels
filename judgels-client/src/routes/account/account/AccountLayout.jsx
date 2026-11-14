import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import UserRoute from '../../../components/UserRoute/UserRoute';
import {
  isTrainingAccountPasswordResetEnabled,
  isTrainingAccountRegistrationEnabled,
} from '../../../modules/webConfig';

export default function AccountLayout() {
  const sidebarItems = [
    {
      path: 'info',
      title: 'Info',
      visible: isTrainingAccountRegistrationEnabled(),
    },
    {
      path: 'avatar',
      title: 'Change avatar',
      visible: isTrainingAccountRegistrationEnabled(),
    },
    {
      path: 'password',
      title: 'Reset password',
      visible: isTrainingAccountPasswordResetEnabled(),
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
