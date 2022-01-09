import { TimelineLineChart, User } from '@blueprintjs/icons';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import UsersPage from './users/UsersPage/UsersPage';
import RatingsPage from './ratings/RatingsPage/RatingsPage';

function SystemRoutes() {
  const sidebarItems = [
    {
      id: 'users',
      titleIcon: <User />,
      title: 'Users',
      routeComponent: Route,
      component: UsersPage,
    },
    {
      id: 'ratings',
      titleIcon: <TimelineLineChart />,
      title: 'Ratings',
      routeComponent: Route,
      component: RatingsPage,
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
