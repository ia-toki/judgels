import { combineReducers } from 'redux';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';

import courseReducer from '../../routes/courses/courses/modules/courseReducer';
import courseChapterReducer from '../../routes/courses/courses/single/chapters/modules/courseChapterReducer';
import courseChaptersReducer from '../../routes/courses/courses/single/chapters/modules/courseChaptersReducer';
import problemSetReducer from '../../routes/problems/problemsets/modules/problemSetReducer';
import problemSetProblemReducer from '../../routes/problems/problemsets/single/problems/modules/problemSetProblemReducer';

export default combineReducers({
  course: persistReducer({ key: 'jerahmeelCourse', storage }, courseReducer),
  courseChapter: persistReducer({ key: 'jerahmeelCourseChapter', storage }, courseChapterReducer),
  courseChapters: courseChaptersReducer,
  problemSet: persistReducer({ key: 'jerahmeelProblemSet', storage }, problemSetReducer),
  problemSetProblem: persistReducer({ key: 'jerahmeelProblemSetProblem', storage }, problemSetProblemReducer),
});
