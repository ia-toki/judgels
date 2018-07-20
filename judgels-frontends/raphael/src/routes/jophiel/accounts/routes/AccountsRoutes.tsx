import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../../../components/layouts/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import UsersPage from './users/UsersPage/UsersPage';
import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

const AccountRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'users',
      titleIcon: 'user',
      title: 'Users',
      routeComponent: Route,
      component: UsersPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Accounts',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default withBreadcrumb('Accounts')(AccountRoutes);
