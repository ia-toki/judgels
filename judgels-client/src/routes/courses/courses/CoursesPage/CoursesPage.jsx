import { Callout, Intent } from '@blueprintjs/core';
import { useQuery } from '@tanstack/react-query';

import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { coursesQueryOptions } from '../../../../modules/queries/course';
import { CourseCard } from '../CourseCard/CourseCard';

import './CoursesPage.scss';

export default function CoursesPage() {
  const { data: response } = useQuery(coursesQueryOptions());

  if (!response) {
    return <LoadingContentCard />;
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
    <>
      <Callout intent={Intent.PRIMARY} icon={null}>
        <HtmlText>{curriculum.description}</HtmlText>
      </Callout>
      <hr />
      <div className="courses">
        {courses.map(course => (
          <CourseCard key={course.jid} course={course} progress={courseProgressesMap[course.jid]} />
        ))}
      </div>
    </>
  );
}
