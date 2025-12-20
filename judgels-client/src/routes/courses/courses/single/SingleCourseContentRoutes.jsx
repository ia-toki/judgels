import { Route, Switch } from 'react-router';

import CourseOverview from './CourseOverview/CourseOverview';
import MainSingleCourseChapterRoutes from './chapters/single/MainSingleCourseChapterRoutes';

export default function SingleCourseContentRoutes() {
  return (
    <Switch>
      <Route path="/courses/:courseSlug/chapters/:chapterAlias" component={MainSingleCourseChapterRoutes} />
      <Route path="/courses/:courseSlug" component={CourseOverview} />
    </Switch>
  );
}
