import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import { ScrollToTopOnMount } from '../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../components/ContentWithSidebar/ContentWithSidebar';
import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import CoursesPage from './courses/CoursesPage/CoursesPage';
import ChaptersPage from './chapters/ChaptersPage/ChaptersPage';
import ArchivesPage from './archives/ArchivesPage/ArchivesPage';
import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';

const TrainingRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'courses',
      titleIcon: 'predictive-analysis',
      title: 'Courses',
      routeComponent: Route,
      component: CoursesPage,
    },
    {
      id: 'chapters',
      titleIcon: 'properties',
      title: 'Chapters',
      routeComponent: Route,
      component: ChaptersPage,
    },
    {
      id: 'archives',
      titleIcon: 'box',
      title: 'Archives',
      routeComponent: Route,
      component: ArchivesPage,
    },
    {
      id: 'problemsets',
      titleIcon: 'panel-stats',
      title: 'Problemsets',
      routeComponent: Route,
      component: ProblemSetsPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Training',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ScrollToTopOnMount />
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default withBreadcrumb('Training')(TrainingRoutes);
