import { combineReducers } from 'redux';

import { roleReducer, RoleState } from './roleReducer';
import { profileReducer, ProfileState } from './profileReducer';

export interface JophielState {
  role: RoleState;
  profile: ProfileState;
}

export const jophielReducer = combineReducers<JophielState>({
  role: roleReducer,
  profile: profileReducer,
});
