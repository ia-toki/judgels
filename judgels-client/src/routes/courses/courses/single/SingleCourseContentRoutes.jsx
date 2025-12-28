import { Route, Routes } from 'react-router-dom';

import CourseOverview from './CourseOverview/CourseOverview';
import MainSingleCourseChapterRoutes from './chapters/single/MainSingleCourseChapterRoutes';

export default function SingleCourseContentRoutes() {
  return (
    <Routes>
      <Route path="chapters/:chapterAlias/*" element={<MainSingleCourseChapterRoutes />} />
      <Route path="*" element={<CourseOverview />} />
    </Routes>
  );
}
