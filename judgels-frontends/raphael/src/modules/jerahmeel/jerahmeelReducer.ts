import { combineReducers } from 'redux';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';

import { courseReducer, CourseState } from '../../routes/courses/courses/modules/courseReducer';

export interface JerahmeelState {
  course: CourseState;
}

export const jerahmeelReducer = combineReducers<JerahmeelState>({
  course: persistReducer({ key: 'jerahmeelCourse', storage }, courseReducer),
});
