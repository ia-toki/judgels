import { setWith, TypedAction, TypedReducer } from 'redoodle';

import { CourseChapter } from '../../../../../../modules/api/jerahmeel/courseChapter';

export interface CourseChapterState {
  value?: CourseChapter;
  name?: string;
}

export const INITIAL_STATE: CourseChapterState = {};

export const PutCourseChapter = TypedAction.define('jerahmeel/courseChapter/PUT')<{
  value: CourseChapter;
  name: string;
}>();
export const DelCourseChapter = TypedAction.defineWithoutPayload('jerahmeel/courseChapter/DEL')();

function createCourseChapterReducer() {
  const builder = TypedReducer.builder<CourseChapterState>();

  builder.withHandler(PutCourseChapter.TYPE, (state, payload) => setWith(state, payload));
  builder.withHandler(DelCourseChapter.TYPE, () => ({ value: undefined, name: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const courseChapterReducer = createCourseChapterReducer();
