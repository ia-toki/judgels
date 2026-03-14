import { Property, TimelineLineChart } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullWidthPageLayout } from '../../components/FullWidthPageLayout/FullWidthPageLayout';

export default function RankingLayout() {
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
    <FullWidthPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullWidthPageLayout>
  );
}
