import { ListDetailView, Property, TimelineLineChart } from '@blueprintjs/icons';
import { Route, withRouter } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import RatingSystemPage from './ratings/RatingSystemPage/RatingSystemPage';
import RatingsPage from './ratings/RatingsPage/RatingsPage';
import ScoresPage from './scores/ScoresPage/ScoresPage';

function RankingRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: <TimelineLineChart />,
      title: 'Top ratings',
      routeComponent: Route,
      component: RatingsPage,
    },
    {
      id: 'rating-system',
      titleIcon: <Property />,
      title: 'Rating system',
      routeComponent: Route,
      component: RatingSystemPage,
    },
    {
      id: 'scores',
      titleIcon: <ListDetailView />,
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
