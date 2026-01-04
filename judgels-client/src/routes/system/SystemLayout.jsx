import { TimelineLineChart } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';

export default function SystemLayout() {
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
