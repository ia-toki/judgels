import * as React from 'react';

import { FullPageLayout } from '../../../../components/layouts/FullPageLayout/FullPageLayout';
import UserRoute from '../../../../components/UserRoute/UserRoute';
import ProfilePage from './profile/ProfilePage/ProfilePage';
import ChangePasswordPage from './changePassword/ChangePasswordPage/ChangePasswordPage';
import ChangeAvatarPage from './changeAvatar/ChangeAvatarPage/ChangeAvatarPage';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

const AccountRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'profile',
      title: 'Profile',
      routeComponent: UserRoute,
      component: ProfilePage,
    },
    {
      id: 'avatar',
      title: 'Change avatar',
      routeComponent: UserRoute,
      component: ChangeAvatarPage,
    },
    {
      id: 'password',
      title: 'Change password',
      routeComponent: UserRoute,
      component: ChangePasswordPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'My account',
    items: sidebarItems,
    smallContent: true,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default withBreadcrumb('My account')(AccountRoutes);
