import { Manual, PanelStats } from '@blueprintjs/icons';
import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ProblemTagFilter from './problems/ProblemTagFilter/ProblemTagFilter';
import ProblemsPage from './problems/ProblemsPage/ProblemsPage';
import ProblemSetArchiveFilter from './problemsets/ProblemSetArchiveFilter/ProblemSetArchiveFilter';
import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';
import { SingleProblemSetLayout, singleProblemSetRoutes } from './problemsets/single/SingleProblemSetRoutes';
import {
  SingleProblemSetProblemLayout,
  singleProblemSetProblemRoutes,
} from './problemsets/single/problems/single/SingleProblemSetProblemRoutes';

import './ProblemsRoutes.scss';

function MainProblemsLayout() {
  return <Outlet />;
}

const MainProblemsLayoutWithBreadcrumb = withBreadcrumb('Problems')(MainProblemsLayout);

function ProblemsLayout() {
  const sidebarItems = [
    {
      path: '',
      titleIcon: <Manual />,
      title: 'Browse problems',
      widgetComponent: ProblemTagFilter,
    },
    {
      path: 'problemsets',
      titleIcon: <PanelStats />,
      title: 'Browse problemsets',
      widgetComponent: ProblemSetArchiveFilter,
    },
  ];

  return (
    <FullPageLayout>
      <ContentWithSidebar title="Menu" items={sidebarItems} basePath="/problems">
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}

export const routes = [
  {
    path: 'problems',
    element: <MainProblemsLayoutWithBreadcrumb />,
    children: [
      {
        path: 'problemsets',
        element: <ProblemsLayout />,
        children: [
          {
            index: true,
            element: <ProblemSetsPage />,
          },
        ],
      },
      {
        path: ':problemSetSlug',
        element: <SingleProblemSetLayout />,
        children: singleProblemSetRoutes,
      },

      {
        path: ':problemSetSlug/:problemAlias',
        element: <SingleProblemSetProblemLayout />,
        children: singleProblemSetProblemRoutes,
      },
      {
        element: <ProblemsLayout />,
        children: [
          {
            index: true,
            element: <ProblemsPage />,
          },
        ],
      },
    ],
  },
];
