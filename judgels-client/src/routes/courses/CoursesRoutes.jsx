import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { FullPageLayout } from '../../components/FullPageLayout/FullPageLayout';
import CoursesPage from './courses/CoursesPage/CoursesPage';
import { SingleCourseLayout, singleCourseRoutes } from './courses/single/SingleCourseRoutes';

function MainCoursesLayout() {
  return (
    <div>
      <Outlet />
    </div>
  );
}

const MainCoursesLayoutWithBreadcrumb = withBreadcrumb('Courses')(MainCoursesLayout);

function CoursesLayout() {
  return (
    <FullPageLayout>
      <Outlet />
    </FullPageLayout>
  );
}

export const routes = [
  {
    path: 'courses',
    element: <MainCoursesLayoutWithBreadcrumb />,
    children: [
      {
        path: ':courseSlug',
        element: <SingleCourseLayout />,
        children: singleCourseRoutes,
      },
      {
        element: <CoursesLayout />,
        children: [
          {
            index: true,
            element: <CoursesPage />,
          },
        ],
      },
    ],
  },
];
