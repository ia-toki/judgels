import { setWith, TypedAction, TypedReducer } from 'redoodle';

import { Course } from '../../../../modules/api/jerahmeel/course';

export interface CourseState {
  value?: Course;
}

export const INITIAL_STATE: CourseState = {};

export const PutCourse = TypedAction.define('jerahmeel/course/PUT')<Course>();
export const DelCourse = TypedAction.defineWithoutPayload('jerahmeel/course/DEL')();

function createCourseReducer() {
  const builder = TypedReducer.builder<CourseState>();

  builder.withHandler(PutCourse.TYPE, (state, payload) => setWith(state, { value: payload }));
  builder.withHandler(DelCourse.TYPE, () => ({ value: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const courseReducer = createCourseReducer();
