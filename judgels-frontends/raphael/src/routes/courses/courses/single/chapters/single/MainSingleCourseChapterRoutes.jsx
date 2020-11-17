import * as React from 'react';
import { Route, withRouter } from 'react-router';

import SingleCourseChapterRoutes from './SingleCourseChapterRoutes';
import SingleCourseChapterDataRoute from './SingleCourseChapterDataRoute';

const MainSingleCourseChapterRoutes = () => (
  <div>
    <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterDataRoute} />
    <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={SingleCourseChapterRoutes} />
  </div>
);

export default withRouter<any, any>(MainSingleCourseChapterRoutes);
