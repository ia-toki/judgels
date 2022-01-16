import { Route, withRouter } from 'react-router';

import SingleCourseDataRoute from './SingleCourseDataRoute';
import SingleCourseRoutes from './SingleCourseRoutes';

function MainSingleCourseRoutes() {
  return (
    <div>
      <Route path="/courses/:courseSlug" component={SingleCourseDataRoute} />
      <Route path="/courses/:courseSlug" component={SingleCourseRoutes} />
    </div>
  );
}

export default withRouter(MainSingleCourseRoutes);
