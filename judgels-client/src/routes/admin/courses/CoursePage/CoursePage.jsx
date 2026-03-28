import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { courseBySlugQueryOptions } from '../../../../modules/queries/course';
import { CourseChaptersSection } from '../CourseChaptersSection/CourseChaptersSection';
import { CourseGeneralSection } from '../CourseGeneralSection/CourseGeneralSection';

export default function CoursePage() {
  const { courseSlug } = useParams({ strict: false });

  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));

  return (
    <ContentCard title={`Courses › ${course.slug}`}>
      <CourseGeneralSection course={course} />
      <hr />
      <CourseChaptersSection course={course} />
    </ContentCard>
  );
}
