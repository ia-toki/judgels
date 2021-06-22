import { combineReducers } from 'redux';

import userWebReducer from '../../routes/jophiel/modules/userWebReducer';
import profileReducer from '../../routes/jophiel/modules/profileReducer';

export default combineReducers({
  userWeb: userWebReducer,
  profile: profileReducer,
});
