import { Box, PanelStats, PredictiveAnalysis, Properties } from '@blueprintjs/icons';
import { Route } from 'react-router';

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
      id: 'courses',
      titleIcon: <PredictiveAnalysis />,
      title: 'Courses',
      routeComponent: Route,
      component: CoursesPage,
    },
    {
      id: 'chapters',
      titleIcon: <Properties />,
      title: 'Chapters',
      routeComponent: Route,
      component: ChaptersPage,
    },
    {
      id: 'archives',
      titleIcon: <Box />,
      title: 'Archives',
      routeComponent: Route,
      component: ArchivesPage,
    },
    {
      id: 'problemsets',
      titleIcon: <PanelStats />,
      title: 'Problemsets',
      routeComponent: Route,
      component: ProblemSetsPage,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Training',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

export default withBreadcrumb('Training')(TrainingRoutes);
