import { Property, TimelineLineChart } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';

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

export default withBreadcrumb('Ranking')(RankingLayout);
