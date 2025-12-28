import { Route, Routes } from 'react-router-dom';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import CoursesRoutes from './CoursesRoutes';
import MainSingleCourseRoutes from './courses/single/MainSingleCourseRoutes';

function MainCoursesRoutes() {
  return (
    <div>
      <Routes>
        <Route path=":courseSlug/*" element={<MainSingleCourseRoutes />} />
        <Route path="*" element={<CoursesRoutes />} />
      </Routes>
    </div>
  );
}

export default withBreadcrumb('Courses')(MainCoursesRoutes);
