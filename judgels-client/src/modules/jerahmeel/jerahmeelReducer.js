import { combineReducers } from 'redux';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';

import chapterProblemReducer from '../../routes/courses/courses/single/chapters/single/problems/single/modules/chapterProblemReducer';
import problemSetReducer from '../../routes/problems/problemsets/modules/problemSetReducer';
import problemSetProblemReducer from '../../routes/problems/problemsets/single/problems/modules/problemSetProblemReducer';

export default combineReducers({
  chapterProblem: chapterProblemReducer,
  problemSet: persistReducer({ key: 'jerahmeelProblemSet', storage }, problemSetReducer),
  problemSetProblem: persistReducer({ key: 'jerahmeelProblemSetProblem', storage }, problemSetProblemReducer),
});
