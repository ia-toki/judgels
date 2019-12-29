import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../components/ContentWithSidebar/ContentWithSidebar';

import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';
import ProblemSetArchiveFilter from './problemsets/ProblemSetArchiveFilter/ProblemSetArchiveFilter';

const ProblemsRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: '@',
      titleIcon: 'panel-stats',
      title: 'Filter by problemset',
      routeComponent: Route,
      component: ProblemSetsPage,
      widgetComponent: ProblemSetArchiveFilter,
    },
  ];

  const contentWithSidebarProps: ContentWithSidebarProps = {
    title: 'Problems Menu',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
};

export default ProblemsRoutes;
