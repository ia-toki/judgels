import { TimelineLineChart } from '@blueprintjs/icons';
import { Navigate, Route, Routes } from 'react-router-dom';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import RatingsPage from './ratings/RatingsPage/RatingsPage';

function SystemRoutes() {
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
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Routes>
          <Route index element={<Navigate to="ratings" replace />} />
          <Route path="ratings" element={<RatingsPage />} />
        </Routes>
      </ContentWithSidebar>
    </FullPageLayout>
  );
}

export default withBreadcrumb('System')(SystemRoutes);
