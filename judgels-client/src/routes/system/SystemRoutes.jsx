import { TimelineLineChart } from '@blueprintjs/icons';
import { Navigate, Outlet } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { isTLX } from '../../conf';
import RatingsPage from './ratings/RatingsPage/RatingsPage';

function SystemLayout() {
  const sidebarItems = [
    {
      path: 'ratings',
      titleIcon: <TimelineLineChart />,
      title: 'Ratings',
    },
  ];

  const contentWithSidebarProps = {
    title: 'System',
    items: sidebarItems,
    basePath: '/system',
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}

const SystemLayoutWithBreadcrumb = withBreadcrumb('System')(SystemLayout);

export const systemRoutes = isTLX()
  ? [
      {
        path: 'system',
        element: <SystemLayoutWithBreadcrumb />,
        children: [
          {
            index: true,
            element: <Navigate to="ratings" replace />,
          },
          {
            path: 'ratings',
            element: <RatingsPage />,
          },
        ],
      },
    ]
  : [];
