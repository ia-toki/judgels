import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { courseBySlugQueryOptions } from '../../../../../../modules/queries/course';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { createDocumentTitle } from '../../../../../../utils/title';
import { selectCourseChapter } from '../modules/courseChapterSelectors';

import * as courseChapterActions from '../modules/courseChapterActions';

export default function SingleCourseChapterLayout() {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const dispatch = useDispatch();
  const token = useSelector(selectToken);
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const chapter = useSelector(selectCourseChapter);

  const loadCourseChapter = async () => {
    const loadedChapter = await dispatch(courseChapterActions.getChapter(course.jid, course.slug, chapterAlias));
    document.title = createDocumentTitle(`${chapterAlias}. ${loadedChapter.name}`);
  };

  useEffect(() => {
    loadCourseChapter();

    return () => {
      dispatch(courseChapterActions.clearChapter());
    };
  }, [course?.jid, chapterAlias]);

  // Optimization:
  // We wait until we get the chapter from the backend only if the current chapter is different from the persisted one.
  if (!chapter || chapter.courseSlug !== courseSlug || chapter.alias !== chapterAlias) {
    return <LoadingState large />;
  }

  return <Outlet />;
}
