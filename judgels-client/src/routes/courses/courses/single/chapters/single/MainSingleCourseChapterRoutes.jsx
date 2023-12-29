import { Route, withRouter } from 'react-router';

import SingleCourseChapterDataRoute from './SingleCourseChapterDataRoute';
import SingleCourseChapterRoutes from './SingleCourseChapterRoutes';

function MainSingleCourseChapterRoutes() {
  return (
    <>
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterDataRoute} />
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterRoutes} />
    </>
  );
}

export default withRouter(MainSingleCourseChapterRoutes);
