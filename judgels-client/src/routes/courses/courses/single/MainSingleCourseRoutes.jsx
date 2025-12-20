import { Route } from 'react-router';

import SingleCourseDataRoute from './SingleCourseDataRoute';
import SingleCourseRoutes from './SingleCourseRoutes';

export default function MainSingleCourseRoutes() {
  return (
    <div>
      <Route path="/courses/:courseSlug" component={SingleCourseDataRoute} />
      <Route path="/courses/:courseSlug" component={SingleCourseRoutes} />
    </div>
  );
}
