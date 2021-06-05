import { User } from '@blueprintjs/icons';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import UsersPage from './users/UsersPage/UsersPage';

function SystemRoutes() {
  const sidebarItems = [
    {
      id: 'users',
      titleIcon: <User />,
      title: 'Users',
      routeComponent: Route,
      component: UsersPage,
    },
  ];

  const contentWithSidebarProps = {
    title: 'System',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

export default withBreadcrumb('System')(SystemRoutes);
