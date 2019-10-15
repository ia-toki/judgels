import * as React from 'react';
import { Route, withRouter } from 'react-router';

import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';

import CoursesPage from './CoursesPage/CoursesPage';
import ChaptersPage from './ChaptersPage/ChaptersPage';

const CourseRoutes = () => (
  <div>
    <Route exact path="/training/courses" component={CoursesPage} />
    <Route path="/training/courses/:courseId" component={ChaptersPage} />
  </div>
);

export default withRouter<any, any>(withBreadcrumb('Courses')(CourseRoutes));
