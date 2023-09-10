import { Route, withRouter } from 'react-router';

import SingleCourseChapterRoutes from './SingleCourseChapterRoutes';
import SingleCourseChapterDataRoute from './SingleCourseChapterDataRoute';

function MainSingleCourseChapterRoutes() {
  return (
    <>
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterDataRoute} />
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterRoutes} />
    </>
  );
}

export default withRouter(MainSingleCourseChapterRoutes);
