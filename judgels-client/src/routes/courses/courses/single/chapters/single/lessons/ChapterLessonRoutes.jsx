import { Route } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterLessonPage from './single/ChapterLessonPage/ChapterLessonPage.jsx';

function ChapterLessonRoutes() {
  return (
    <Route path="/courses/:courseSlug/chapters/:chapterAlias/lessons/:lessonAlias" component={ChapterLessonPage} />
  );
}

export default withBreadcrumb('Lessons')(ChapterLessonRoutes);
