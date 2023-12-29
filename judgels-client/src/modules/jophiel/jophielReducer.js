import { combineReducers } from 'redux';

import profileReducer from '../../routes/jophiel/modules/profileReducer';
import userWebReducer from '../../routes/jophiel/modules/userWebReducer';

export default combineReducers({
  userWeb: userWebReducer,
  profile: profileReducer,
});
