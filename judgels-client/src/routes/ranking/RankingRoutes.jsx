import { Property, TimelineLineChart } from '@blueprintjs/icons';
import { Route, Routes } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import RatingSystemPage from './ratings/RatingSystemPage/RatingSystemPage';
import RatingsPage from './ratings/RatingsPage/RatingsPage';

function RankingRoutes() {
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
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Routes>
          <Route index element={<RatingsPage />} />
          <Route path="rating-system" element={<RatingSystemPage />} />
        </Routes>
      </ContentWithSidebar>
    </FullPageLayout>
  );
}

export default withBreadcrumb('Ranking')(RankingRoutes);
