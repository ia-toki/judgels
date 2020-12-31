import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';
import ProblemSetArchiveFilter from './problemsets/ProblemSetArchiveFilter/ProblemSetArchiveFilter';

function ProblemsRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: 'panel-stats',
      title: 'Filter by problemset',
      routeComponent: Route,
      component: ProblemSetsPage,
      widgetComponent: ProblemSetArchiveFilter,
    },
  ];

  const contentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps} />
    </FullPageLayout>
  );
}

export default ProblemsRoutes;
