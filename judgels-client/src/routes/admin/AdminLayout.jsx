import { Box, PanelStats, People, PredictiveAnalysis, Properties, Shield, TimelineLineChart } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullWidthPageLayout } from '../../components/FullWidthPageLayout/FullWidthPageLayout';

export default function AdminLayout() {
  const sidebarItems = [
    {
      path: 'users',
      titleIcon: <People />,
      title: 'Users',
    },
    {
      path: 'roles',
      titleIcon: <Shield />,
      title: 'Roles',
    },
    {
      path: 'ratings',
      titleIcon: <TimelineLineChart />,
      title: 'Ratings',
    },
    {
      path: 'courses',
      titleIcon: <PredictiveAnalysis />,
      title: 'Courses',
    },
    {
      path: 'chapters',
      titleIcon: <Properties />,
      title: 'Chapters',
    },
    {
      path: 'archives',
      titleIcon: <Box />,
      title: 'Archives',
    },
    {
      path: 'problemsets',
      titleIcon: <PanelStats />,
      title: 'Problemsets',
    },
  ];

  const contentWithSidebarProps = {
    title: 'Admin',
    items: sidebarItems,
    basePath: '/admin',
  };

  return (
    <FullWidthPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullWidthPageLayout>
  );
}
