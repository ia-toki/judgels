import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterProblemPage from './single/ChapterProblemPage/ChapterProblemPage';
import { chapterProblemRoutes as chapterProblemChildRoutes } from './single/ChapterProblemRoutes';

export const chapterProblemRoutes = [
  {
    path: ':problemAlias',
    element: <ChapterProblemPage />,
    children: chapterProblemChildRoutes,
  },
];

function ChapterProblemLayout() {
  return <Outlet />;
}

export default withBreadcrumb('Problems')(ChapterProblemLayout);
