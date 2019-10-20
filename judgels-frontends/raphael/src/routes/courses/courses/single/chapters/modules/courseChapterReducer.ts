import { setWith, TypedAction, TypedReducer } from 'redoodle';

import { CourseChapter } from '../../../../../../modules/api/jerahmeel/courseChapter';
import { Chapter } from '../../../../../../modules/api/jerahmeel/chapter';

export interface CourseChapterState {
  courseChapter?: CourseChapter;
  chapter?: Chapter;
}

export const INITIAL_STATE: CourseChapterState = {};

export const PutCourseChapter = TypedAction.define('jerahmeel/courseChapter/PUT')<{
  courseChapter: CourseChapter;
  chapter: Chapter;
}>();
export const DelCourseChapter = TypedAction.defineWithoutPayload('jerahmeel/courseChapter/DEL')();

function createCourseChapterReducer() {
  const builder = TypedReducer.builder<CourseChapterState>();

  builder.withHandler(PutCourseChapter.TYPE, (state, payload) => setWith(state, payload));
  builder.withHandler(DelCourseChapter.TYPE, () => ({ courseChapter: undefined, chapter: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const courseChapterReducer = createCourseChapterReducer();
