import * as React from 'react';
import { Route } from 'react-router';

import { FullPageLayout } from '../../../../components/layouts/FullPageLayout/FullPageLayout';
import ContentWithSidebar, {
  ContentWithSidebarItem,
  ContentWithSidebarProps,
} from '../../../../components/ContentWithSidebar/ContentWithSidebar';
import ProblemsetListPage from './problemsets/routes/list/ProblemsetListPage/ProblemsetListPage';

const MainTrainingRoutes = () => {
  const sidebarItems: ContentWithSidebarItem[] = [
    {
      id: 'problemsets',
      titleIcon: 'projects',
      title: 'Problemsets',
      routeComponent: Route,
      component: ProblemsetListPage,
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
