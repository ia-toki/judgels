import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../components/ContentWithSidebar/ContentWithSidebar';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import UsersPage from './users/UsersPage/UsersPage';

const SystemRoutes = () => {
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
    title: 'System',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default withBreadcrumb('System')(SystemRoutes);
