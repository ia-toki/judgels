import { useSuspenseQuery } from '@tanstack/react-query';
import { Outlet, useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { FullWidthPageLayout } from '../../../../components/FullWidthPageLayout/FullWidthPageLayout';
import { ScrollToTopOnMount } from '../../../../components/ScrollToTopOnMount/ScrollToTopOnMount';
import { courseBySlugQueryOptions } from '../../../../modules/queries/course';
import { createDocumentTitle } from '../../../../utils/title';
import CourseChaptersSidebar from './CourseChaptersSidebar/CourseChaptersSidebar';

import './SingleCourseLayout.scss';

export default function SingleCourseLayout() {
  const { courseSlug } = useParams({ strict: false });
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));

  useEffect(() => {
    document.title = createDocumentTitle(course.name);
  }, [courseSlug, course.name]);

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
