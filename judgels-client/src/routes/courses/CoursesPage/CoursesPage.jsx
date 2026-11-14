import { Callout, Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';

import { Card } from '../../../components/Card/Card';
import { ContentList } from '../../../components/ContentList/ContentList';
import { LoadingContentRow } from '../../../components/ContentRow/LoadingContentRow';
import { HtmlText } from '../../../components/HtmlText/HtmlText';
import { coursesQueryOptions } from '../../../modules/queries/course';
import { CourseCard } from '../CourseCard/CourseCard';

export default function CoursesPage() {
  const { data: response } = useQuery(coursesQueryOptions());

  const renderCourses = () => {
    if (!response) {
      return <LoadingContentRow />;
    }

    const { data: courses, curriculum, courseProgressesMap } = response;

    if (courses.length === 0) {
      return (
        <p>
          <small>No courses.</small>
        </p>
      );
    }

    return (
      <Flex flexDirection="column" gap={5}>
        <Callout intent={Intent.PRIMARY} icon={null}>
          <HtmlText>{curriculum.description}</HtmlText>
        </Callout>
        <ContentList>
          {courses.map(course => (
            <CourseCard key={course.jid} course={course} progress={courseProgressesMap[course.jid]} />
          ))}
        </ContentList>
      </Flex>
    );
  };

  return (
    <Card title="Courses" fullHeight>
      {renderCourses()}
    </Card>
  );
}
