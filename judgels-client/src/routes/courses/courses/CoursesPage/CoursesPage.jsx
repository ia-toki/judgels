import { Callout, Intent } from '@blueprintjs/core';
import { useEffect, useState } from 'react';

import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { callAction } from '../../../../modules/callAction';
import { CourseCard } from '../CourseCard/CourseCard';

import * as courseActions from '../modules/courseActions';

import './CoursesPage.scss';

export default function CoursesPage() {
  const [state, setState] = useState({
    response: undefined,
  });

  const refreshCourses = async () => {
    const response = await callAction(courseActions.getCourses());
    setState({ response });
  };

  useEffect(() => {
    refreshCourses();
  }, []);

  const render = () => {
    const { response } = state;
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
  };

  return render();
}
