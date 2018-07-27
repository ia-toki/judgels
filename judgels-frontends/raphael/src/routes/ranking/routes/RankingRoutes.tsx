import * as React from 'react';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../../components/layouts/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../components/ContentWithSidebar/ContentWithSidebar';
import RatingsPage from './ratings/RatingsPage/RatingsPage';

const RankingRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: '@',
      titleIcon: 'timeline-line-chart',
      title: 'Top ratings',
      routeComponent: Route,
      component: RatingsPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Ranking Menu',
    items: sidebarItems,
    smallContent: true,
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default withRouter<any>(withBreadcrumb('Ranking')(RankingRoutes));
