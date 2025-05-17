import { TimelineLineChart } from '@blueprintjs/icons';
import { Route } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import RatingsPage from './ratings/RatingsPage/RatingsPage';

function SystemRoutes() {
  const sidebarItems = [
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
