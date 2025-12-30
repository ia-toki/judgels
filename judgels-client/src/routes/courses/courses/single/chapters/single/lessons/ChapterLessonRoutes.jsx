import { Outlet } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterLessonPage from './single/ChapterLessonPage/ChapterLessonPage.jsx';

export const chapterLessonRoutes = [
  {
    path: ':lessonAlias',
    element: <ChapterLessonPage />,
  },
];

function ChapterLessonLayout() {
  return <Outlet />;
}

export default withBreadcrumb('Lessons')(ChapterLessonLayout);
