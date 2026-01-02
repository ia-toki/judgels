import { Outlet, useLocation, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { FullWidthPageLayout } from '../../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { selectCourse } from '../modules/courseSelectors';
import CourseChaptersSidebar from './CourseChaptersSidebar/CourseChaptersSidebar';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as courseActions from '../modules/courseActions';

import './SingleCourseLayout.scss';

export default function SingleCourseLayout() {
  const { courseSlug } = useParams({ strict: false });
  const { pathname } = useLocation();
  const dispatch = useDispatch();
  const course = useSelector(selectCourse);

  const loadCourse = async () => {
    const loadedCourse = await dispatch(courseActions.getCourseBySlug(courseSlug));
    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, loadedCourse.name));
  };

  useEffect(() => {
    loadCourse();

    return () => {
      dispatch(courseActions.clearCourse());
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
  }, [courseSlug]);

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
