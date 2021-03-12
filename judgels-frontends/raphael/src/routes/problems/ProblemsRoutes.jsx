import { Intent, Tag } from '@blueprintjs/core';
import { Route } from 'react-router';

import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import ProblemsPage from './problems/ProblemsPage/ProblemsPage';
import ProblemTagFilter from './problems/ProblemTagFilter/ProblemTagFilter';
import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';
import ProblemSetArchiveFilter from './problemsets/ProblemSetArchiveFilter/ProblemSetArchiveFilter';

function ProblemsRoutes() {
  const sidebarItems = [
    {
      id: '@',
      titleIcon: 'manual',
      title: (
        <div className="tab-item-with-widget">
          <div className="tab-item-with-widget__name">Browse problems</div>
          <div className="tab-item-with-widget__widget">
            <Tag className="normal-weight" intent={Intent.WARNING}>
              BETA
            </Tag>
          </div>
          <div className="clearfix" />
        </div>
      ),
      routeComponent: Route,
      component: ProblemsPage,
      widgetComponent: ProblemTagFilter,
    },
    {
      id: 'problemsets',
      titleIcon: 'panel-stats',
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
