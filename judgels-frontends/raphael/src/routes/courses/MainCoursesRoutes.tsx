import * as React from 'react';
import { Route, Switch } from 'react-router';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import CoursesRoutes from './CoursesRoutes';

const MainCoursesRoutes = () => (
  <div>
    <Switch>
      <Route path="/courses" component={CoursesRoutes} />
    </Switch>
  </div>
);

export default withBreadcrumb('Courses')(MainCoursesRoutes);
