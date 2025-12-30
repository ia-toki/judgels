import { Property, TimelineLineChart } from '@blueprintjs/icons';
import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import RatingSystemPage from './ratings/RatingSystemPage/RatingSystemPage';
import RatingsPage from './ratings/RatingsPage/RatingsPage';

function RankingLayout() {
  const sidebarItems = [
    {
      path: '',
      titleIcon: <TimelineLineChart />,
      title: 'Top ratings',
    },
    {
      path: 'rating-system',
      titleIcon: <Property />,
      title: 'Rating system',
    },
  ];

  const contentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
    smallContent: true,
    basePath: '/ranking',
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

const RankingLayoutWithBreadcrumb = withBreadcrumb('Ranking')(RankingLayout);

export const routes = [
  {
    path: 'ranking',
    element: <RankingLayoutWithBreadcrumb />,
    children: [
      {
        index: true,
        element: <RatingsPage />,
      },
      {
        path: 'rating-system',
        element: <RatingSystemPage />,
      },
    ],
  },
];
