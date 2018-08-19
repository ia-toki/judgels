import { combineReducers } from 'redux';

import { userWebReducer, UserWebState } from './userWebReducer';
import { profileReducer, ProfileState } from './profileReducer';

export interface JophielState {
  userWeb: UserWebState;
  profile: ProfileState;
}

export const jophielReducer = combineReducers<JophielState>({
  userWeb: userWebReducer,
  profile: profileReducer,
});
