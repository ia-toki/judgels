import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../components/ContentWithSidebar/ContentWithSidebar';

import ProblemsetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';
import ProblemsetSubmissionsPage from './submissions/ProblemSetSubmissionsPage/ProblemSetSubmissionsPage';

const ProblemsRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: '@',
      titleIcon: 'panel-stats',
      title: 'Problemsets',
      routeComponent: Route,
      component: ProblemsetsPage,
    },
    {
      id: 'submissions',
      titleIcon: 'layers',
      title: 'Submissions',
      routeComponent: Route,
      component: ProblemsetSubmissionsPage,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default ProblemsRoutes;
