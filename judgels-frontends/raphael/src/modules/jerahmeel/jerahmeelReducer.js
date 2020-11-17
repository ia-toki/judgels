import { combineReducers } from 'redux';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';

import { courseReducer, CourseState } from '../../routes/courses/courses/modules/courseReducer';
import {
  courseChapterReducer,
  CourseChapterState,
} from '../../routes/courses/courses/single/chapters/modules/courseChapterReducer';
import { ProblemSetState, problemSetReducer } from '../../routes/problems/problemsets/modules/problemSetReducer';
import {
  ProblemSetProblemState,
  problemSetProblemReducer,
} from '../../routes/problems/problemsets/single/problems/modules/problemSetProblemReducer';

export interface JerahmeelState {
  course: CourseState;
  courseChapter: CourseChapterState;
  problemSet: ProblemSetState;
  problemSetProblem: ProblemSetProblemState;
}

export const jerahmeelReducer = combineReducers<JerahmeelState>({
  course: persistReducer({ key: 'jerahmeelCourse', storage }, courseReducer),
  courseChapter: persistReducer({ key: 'jerahmeelCourseChapter', storage }, courseChapterReducer),
  problemSet: persistReducer({ key: 'jerahmeelProblemSet', storage }, problemSetReducer),
  problemSetProblem: persistReducer({ key: 'jerahmeelProblemSetProblem', storage }, problemSetProblemReducer),
});
