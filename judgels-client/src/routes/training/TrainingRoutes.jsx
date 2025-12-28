import { Box, PanelStats, PredictiveAnalysis, Properties } from '@blueprintjs/icons';
import { Navigate, Route, Routes } from 'react-router-dom';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ArchivesPage from './archives/ArchivesPage/ArchivesPage';
import ChaptersPage from './chapters/ChaptersPage/ChaptersPage';
import CoursesPage from './courses/CoursesPage/CoursesPage';
import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';

function TrainingRoutes() {
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
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Routes>
          <Route index element={<Navigate to="courses" replace />} />
          <Route path="courses" element={<CoursesPage />} />
          <Route path="chapters" element={<ChaptersPage />} />
          <Route path="archives" element={<ArchivesPage />} />
          <Route path="problemsets" element={<ProblemSetsPage />} />
        </Routes>
      </ContentWithSidebar>
    </FullPageLayout>
  );
}

export default withBreadcrumb('Training')(TrainingRoutes);
