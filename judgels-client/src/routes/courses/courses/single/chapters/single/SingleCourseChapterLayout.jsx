import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useSelector } from 'react-redux';

import { courseBySlugQueryOptions, courseChapterQueryOptions } from '../../../../../../modules/queries/course';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { createDocumentTitle } from '../../../../../../utils/title';

export default function SingleCourseChapterLayout() {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(token, course.jid, chapterAlias));

  useEffect(() => {
    document.title = createDocumentTitle(`${chapterAlias}. ${chapter.name}`);
  }, [chapterAlias, chapter.name]);

  return <Outlet />;
}
