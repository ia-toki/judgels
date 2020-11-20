import * as React from 'react';

import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import UserRoute from '../../../components/UserRoute/UserRoute';
import ContentWithSidebar from '../../../components/ContentWithSidebar/ContentWithSidebar';
import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import InfoPage from './info/InfoPage/InfoPage';
import ChangePasswordPage from './changePassword/ChangePasswordPage/ChangePasswordPage';
import ChangeAvatarPage from './changeAvatar/ChangeAvatarPage/ChangeAvatarPage';

function AccountRoutes() {
  const sidebarItems = [
    {
      id: 'info',
      title: 'Info',
      routeComponent: UserRoute,
      component: InfoPage,
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

  const contentWithSidebarProps = {
    title: 'My account',
    items: sidebarItems,
    smallContent: true,
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

export default withBreadcrumb('My account')(AccountRoutes);
