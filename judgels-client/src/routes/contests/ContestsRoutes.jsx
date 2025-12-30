import { Console } from '@blueprintjs/icons';
import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContentWithSidebar from '../../components/ContentWithSidebar/ContentWithSidebar';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import ContestsPage from './contests/ContestsPage/ContestsPage';
import { SingleContestLayout, singleContestRoutes } from './contests/single/SingleContestRoutes';

function MainContestsLayout() {
  return (
    <div>
      <Outlet />
    </div>
  );
}

const MainContestsLayoutWithBreadcrumb = withBreadcrumb('Contests')(MainContestsLayout);

function ContestsLayout() {
  const sidebarItems = [
    {
      path: '',
      titleIcon: <Console />,
      title: 'Contests',
    },
  ];

  const contentWithSidebarProps = {
    title: 'Menu',
    items: sidebarItems,
    basePath: '/contests',
  };

  return (
    <FullPageLayout>
      <ContentWithSidebar {...contentWithSidebarProps}>
        <Outlet />
      </ContentWithSidebar>
    </FullPageLayout>
  );
}

export const routes = [
  {
    path: 'contests',
    element: <MainContestsLayoutWithBreadcrumb />,
    children: [
      {
        path: ':contestSlug',
        element: <SingleContestLayout />,
        children: singleContestRoutes,
      },
      {
        element: <ContestsLayout />,
        children: [
          {
            index: true,
            element: <ContestsPage />,
          },
        ],
      },
    ],
  },
];
