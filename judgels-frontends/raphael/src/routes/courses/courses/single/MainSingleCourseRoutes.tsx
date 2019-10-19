import * as React from 'react';
import { Route, withRouter } from 'react-router';

import SingleCourseDataRoute from './SingleCourseDataRoute';

const MainSingleCourseRoutes = () => (
  <div>
    <Route path="/courses/:courseSlug" component={SingleCourseDataRoute} />
  </div>
);

export default withRouter<any, any>(MainSingleCourseRoutes);
