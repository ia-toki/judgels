import { Box, PanelStats, PredictiveAnalysis, Properties } from '@blueprintjs/icons';
import { Navigate, Outlet } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ArchivesPage from './archives/ArchivesPage/ArchivesPage';
import ChaptersPage from './chapters/ChaptersPage/ChaptersPage';
import CoursesPage from './courses/CoursesPage/CoursesPage';
import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';

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

const TrainingLayoutWithBreadcrumb = withBreadcrumb('Training')(TrainingLayout);

export const routes = [
  {
    path: 'training',
    element: <TrainingLayoutWithBreadcrumb />,
    children: [
      {
        index: true,
        element: <Navigate to="courses" replace />,
      },
      {
        path: 'courses',
        element: <CoursesPage />,
      },
      {
        path: 'chapters',
        element: <ChaptersPage />,
      },
      {
        path: 'archives',
        element: <ArchivesPage />,
      },
      {
        path: 'problemsets',
        element: <ProblemSetsPage />,
      },
    ],
  },
];
