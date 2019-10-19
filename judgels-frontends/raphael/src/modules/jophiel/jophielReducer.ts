import { combineReducers } from 'redux';

import { userWebReducer, UserWebState } from '../../routes/jophiel/modules/userWebReducer';
import { profileReducer, ProfileState } from '../../routes/jophiel/modules/profileReducer';

export interface JophielState {
  userWeb: UserWebState;
  profile: ProfileState;
}

export const jophielReducer = combineReducers<JophielState>({
  userWeb: userWebReducer,
  profile: profileReducer,
});
