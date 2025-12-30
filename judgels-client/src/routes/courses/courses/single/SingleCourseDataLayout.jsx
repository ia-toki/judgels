import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useParams } from 'react-router';

import { useBreadcrumbsPath } from '../../../../hooks/useBreadcrumbsPath';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as courseActions from '../modules/courseActions';

export default function SingleCourseDataLayout() {
  const { courseSlug } = useParams();
  const pathname = useBreadcrumbsPath();
  const dispatch = useDispatch();

  const loadCourse = async () => {
    const course = await dispatch(courseActions.getCourseBySlug(courseSlug));
    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, course.name));
  };

  useEffect(() => {
    loadCourse();

    return () => {
      dispatch(courseActions.clearCourse());
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
  }, []);

  return null;
}
