import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useRouteMatch } from 'react-router-dom';

import { selectCourse } from '../../../modules/courseSelectors';

import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as courseChapterActions from '../modules/courseChapterActions';

export default function SingleCourseChapterDataRoute() {
  const { courseSlug, chapterAlias } = useParams();
  const match = useRouteMatch();
  const dispatch = useDispatch();
  const course = useSelector(selectCourse);

  const loadCourseChapter = async () => {
    if (!course || course.slug !== courseSlug) {
      return;
    }
    const chapter = await dispatch(courseChapterActions.getChapter(course.jid, course.slug, chapterAlias));
    dispatch(breadcrumbsActions.pushBreadcrumb(match.url, `${chapterAlias}. ${chapter.name}`));
  };

  useEffect(() => {
    loadCourseChapter();

    return () => {
      dispatch(courseChapterActions.clearChapter());
      dispatch(breadcrumbsActions.popBreadcrumb(match.url));
    };
  }, [course?.jid, chapterAlias]);

  return null;
}
