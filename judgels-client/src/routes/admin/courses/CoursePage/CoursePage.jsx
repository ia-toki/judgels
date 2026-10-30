import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { EditableSections } from '../../../../components/EditableSections/EditableSections';
import { courseBySlugQueryOptions } from '../../../../modules/queries/course';
import { CourseChaptersSection } from '../CourseChaptersSection/CourseChaptersSection';
import { CourseGeneralSection } from '../CourseGeneralSection/CourseGeneralSection';

export default function CoursePage() {
  const { courseSlug } = useParams({ strict: false });

  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));

  return (
    <ContentCard title={<BreadcrumbTitle parent="Courses" current={course.slug} />}>
      <EditableSections>
        <Flex flexDirection="column" gap={8}>
          <CourseGeneralSection course={course} />
          <CourseChaptersSection course={course} />
        </Flex>
      </EditableSections>
    </ContentCard>
  );
}
