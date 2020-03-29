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
