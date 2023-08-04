import { TimelineLineChart } from '@blueprintjs/icons';
import { Route } from 'react-router';

import { isTLX } from '../../conf';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import RatingsPage from './ratings/RatingsPage/RatingsPage';

function SystemRoutes() {
  const sidebarItems = [
    ...(isTLX()
      ? [
          {
            id: 'ratings',
            titleIcon: <TimelineLineChart />,
            title: 'Ratings',
            routeComponent: Route,
            component: RatingsPage,
          },
        ]
      : []),
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
