import { Box, PanelStats, PredictiveAnalysis, Properties } from '@blueprintjs/icons';
import { Outlet } from '@tanstack/react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';

function TrainingLayout() {
  const sidebarItems = [
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
    title: 'Training',
    items: sidebarItems,
    basePath: '/training',
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

export default withBreadcrumb('Training')(TrainingLayout);
