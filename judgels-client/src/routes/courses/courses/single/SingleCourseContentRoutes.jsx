import { Route, Switch, withRouter } from 'react-router';

import MainSingleCourseChapterRoutes from './chapters/single/MainSingleCourseChapterRoutes';
import CourseOverview from './CourseOverview/CourseOverview';

function SingleCourseContentRoutes() {
  return (
    <div>
      <Switch>
        <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={MainSingleCourseChapterRoutes} />
        <Route path="/courses/:courseSlug" component={CourseOverview} />
      </Switch>
    </div>
  );
}

export default withRouter(SingleCourseContentRoutes);
