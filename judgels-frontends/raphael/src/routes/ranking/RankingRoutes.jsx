import * as React from 'react';
import { Route, withRouter } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import RatingsPage from './ratings/RatingsPage/RatingsPage';
import RatingSystemPage from './ratings/RatingSystemPage/RatingSystemPage';
import ScoresPage from './scores/ScoresPage/ScoresPage';

function RankingRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: 'timeline-line-chart',
      title: 'Top ratings',
      routeComponent: Route,
      component: RatingsPage,
    },
    {
      id: 'rating-system',
      titleIcon: 'property',
      title: 'Rating system',
      routeComponent: Route,
      component: RatingSystemPage,
    },
    {
      id: 'scores',
      titleIcon: 'list-detail-view',
      title: 'Top scorers',
      routeComponent: Route,
      component: ScoresPage,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Menu',
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

export default withRouter(withBreadcrumb('Ranking')(RankingRoutes));
