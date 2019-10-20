import { combineReducers } from 'redux';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';

import { courseReducer, CourseState } from '../../routes/courses/courses/modules/courseReducer';
import {
  courseChapterReducer,
  CourseChapterState,
} from '../../routes/courses/courses/single/chapters/modules/courseChapterReducer';

export interface JerahmeelState {
  course: CourseState;
  courseChapter: CourseChapterState;
}

export const jerahmeelReducer = combineReducers<JerahmeelState>({
  course: persistReducer({ key: 'jerahmeelCourse', storage }, courseReducer),
  courseChapter: persistReducer({ key: 'jerahmeelCourseChapter', storage }, courseChapterReducer),
});
