import { useSelector } from 'react-redux';
import { Outlet, useParams } from 'react-router';

import { FullWidthPageLayout } from '../../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { selectCourse } from '../modules/courseSelectors';
import CourseChaptersSidebar from './CourseChaptersSidebar/CourseChaptersSidebar';
import CourseOverview from './CourseOverview/CourseOverview';
import SingleCourseDataLayout from './SingleCourseDataLayout';
import { SingleCourseChapterLayout, singleCourseChapterRoutes } from './chapters/single/SingleCourseChapterRoutes';

import './SingleCourseRoutes.scss';

export const singleCourseRoutes = [
  {
    index: true,
    element: <CourseOverview />,
  },
  {
    path: 'chapters/:chapterAlias',
    element: <SingleCourseChapterLayout />,
    children: singleCourseChapterRoutes,
  },
];

export function SingleCourseLayout() {
  return (
    <>
      <SingleCourseDataLayout />
      <MainSingleCourseLayout />
    </>
  );
}

function MainSingleCourseLayout() {
  const { courseSlug } = useParams();
  const course = useSelector(selectCourse);

  // Optimization:
  // We wait until we get the course from the backend only if the current slug is different from the persisted one.
  if (!course || course.slug !== courseSlug) {
    return <LoadingState large />;
  }

  return (
    <FullWidthPageLayout>
      <ScrollToTopOnMount />
      <div className="single-course-routes">
        <CourseChaptersSidebar />
        <Outlet />
      </div>
    </FullWidthPageLayout>
  );
}
