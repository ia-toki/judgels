import { Route } from 'react-router';

import SingleCourseChapterDataRoute from './SingleCourseChapterDataRoute';
import SingleCourseChapterRoutes from './SingleCourseChapterRoutes';

export default function MainSingleCourseChapterRoutes() {
  return (
    <>
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterDataRoute} />
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterRoutes} />
    </>
  );
}
