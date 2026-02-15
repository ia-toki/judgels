import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { courseBySlugQueryOptions, courseChapterQueryOptions } from '../../../../../../modules/queries/course';
import { createDocumentTitle } from '../../../../../../utils/title';

export default function SingleCourseChapterLayout() {
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(course.jid, chapterAlias));

  useEffect(() => {
    document.title = createDocumentTitle(`${chapterAlias}. ${chapter.name}`);
  }, [chapterAlias, chapter.name]);

  return <Outlet />;
}
