import { Manual, PanelStats } from '@blueprintjs/icons';
import { Route } from 'react-router';

import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ProblemTagFilter from './problems/ProblemTagFilter/ProblemTagFilter';
import ProblemsPage from './problems/ProblemsPage/ProblemsPage';
import ProblemSetArchiveFilter from './problemsets/ProblemSetArchiveFilter/ProblemSetArchiveFilter';
import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';

import './ProblemsRoutes.scss';

function ProblemsRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: <Manual />,
      title: 'Browse problems',
      routeComponent: Route,
      component: ProblemsPage,
      widgetComponent: ProblemTagFilter,
    },
    {
      id: 'problemsets',
      titleIcon: <PanelStats />,
      title: 'Browse problemsets',
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
