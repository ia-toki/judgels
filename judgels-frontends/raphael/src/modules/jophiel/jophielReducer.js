import { combineReducers } from 'redux';

import userWebReducer from '../../routes/jophiel/modules/userWebReducer';
import profileReducer from '../../routes/jophiel/modules/profileReducer';
import webReducer from '../../routes/jophiel/modules/webReducer';

export default combineReducers({
  userWeb: userWebReducer,
  profile: profileReducer,
  web: webReducer,
});
