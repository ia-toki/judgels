import * as React from 'react';
import { Route, withRouter, Switch } from 'react-router';

import SingleCourseDataRoute from './SingleCourseDataRoute';
import SingleCourseRoutes from './SingleCourseRoutes';
import MainSingleCourseChapterRoutes from './chapters/single/MainSingleCourseChapterRoutes';

function MainSingleCourseRoutes() {
  return (
    <div>
      <Route path="/courses/:courseSlug" component={SingleCourseDataRoute} />
      <Switch>
        <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={MainSingleCourseChapterRoutes} />
        <Route path="/courses/:courseSlug" component={SingleCourseRoutes} />
      </Switch>
    </div>
  );
}

export default withRouter(MainSingleCourseRoutes);
