import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../components/ContentWithSidebar/ContentWithSidebar';

import ProblemsetListPage from './problemsets/list/ProblemsetListPage/ProblemsetListPage';
import CourseRoutes from './courses/CourseRoutes';

const MainTrainingRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'problemsets',
      titleIcon: 'projects',
      title: 'Problemsets',
      routeComponent: Route,
      component: ProblemsetListPage,
    },
    {
      id: 'course',
      titleIcon: 'predictive-analysis',
      title: 'Courses',
      routeComponent: Route,
      component: CourseRoutes,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Training',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default MainTrainingRoutes;
