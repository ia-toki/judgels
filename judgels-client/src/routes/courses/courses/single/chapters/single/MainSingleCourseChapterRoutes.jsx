import { Route, withRouter } from 'react-router';

import SingleCourseChapterRoutes from './SingleCourseChapterRoutes';
import SingleCourseChapterDataRoute from './SingleCourseChapterDataRoute';

function MainSingleCourseChapterRoutes() {
  return (
    <div>
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterDataRoute} />
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterRoutes} />
    </div>
  );
}

export default withRouter(MainSingleCourseChapterRoutes);
