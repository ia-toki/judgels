import { Route } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterLessonsPage from './ChapterLessonsPage/ChapterLessonsPage';
import ChapterLessonPage from './single/ChapterLessonPage/ChapterLessonPage.jsx';

function ChapterLessonRoutes() {
  return (
    <div>
      <Route exact path="/courses/:courseSlug/chapters/:chapterAlias" component={ChapterLessonsPage} />
      <Route exact path="/courses/:courseSlug/chapters/:chapterAlias/lessons" component={ChapterLessonsPage} />
      <Route path="/courses/:courseSlug/chapters/:chapterAlias/lessons/:lessonAlias" component={ChapterLessonPage} />
    </div>
  );
}

export default withBreadcrumb('Lessons')(ChapterLessonRoutes);
