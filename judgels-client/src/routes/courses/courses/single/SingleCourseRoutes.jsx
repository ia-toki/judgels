import { useSelector } from 'react-redux';
import { useParams } from 'react-router';

import { FullWidthPageLayout } from '../../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { selectCourse } from '../modules/courseSelectors';
import CourseChaptersSidebar from './CourseChaptersSidebar/CourseChaptersSidebar';
import SingleCourseContentRoutes from './SingleCourseContentRoutes';

import './SingleCourseRoutes.scss';

export default function SingleCourseRoutes() {
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
        <SingleCourseContentRoutes />
      </div>
    </FullWidthPageLayout>
  );
}
