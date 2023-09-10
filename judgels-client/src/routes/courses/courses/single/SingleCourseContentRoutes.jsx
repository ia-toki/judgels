import { Route, Switch, withRouter } from 'react-router';

import MainSingleCourseChapterRoutes from './chapters/single/MainSingleCourseChapterRoutes';
import CourseOverview from './CourseOverview/CourseOverview';

function SingleCourseContentRoutes() {
  return (
    <Switch>
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={MainSingleCourseChapterRoutes} />
      <Route path="/courses/:courseSlug" component={CourseOverview} />
    </Switch>
  );
}

export default withRouter(SingleCourseContentRoutes);
