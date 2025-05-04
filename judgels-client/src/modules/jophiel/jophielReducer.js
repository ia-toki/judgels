import { combineReducers } from 'redux';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';

import profileReducer from '../../routes/jophiel/modules/profileReducer';
import userWebReducer from '../../routes/jophiel/modules/userWebReducer';

export default combineReducers({
  userWeb: persistReducer({ key: 'jophielUserWeb', storage }, userWebReducer),
  profile: profileReducer,
});
