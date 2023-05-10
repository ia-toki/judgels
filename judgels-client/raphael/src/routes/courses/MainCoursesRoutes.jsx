import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import CoursesRoutes from './CoursesRoutes';
import MainSingleCourseRoutes from './courses/single/MainSingleCourseRoutes';

function MainCoursesRoutes() {
  return (
    <div>
      <Switch>
        <Route path="/courses/:courseSlug([a-zA-Z0-9-]+)" component={MainSingleCourseRoutes} />
        <Route path="/courses" component={CoursesRoutes} />
      </Switch>
    </div>
  );
}

export default withBreadcrumb('Courses')(MainCoursesRoutes);
