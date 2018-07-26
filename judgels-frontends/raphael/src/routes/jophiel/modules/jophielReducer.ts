import { combineReducers } from 'redux';

import { webConfigReducer, WebConfigState } from './webConfigReducer';
import { roleReducer, RoleState } from './roleReducer';
import { profileReducer, ProfileState } from './profileReducer';

export interface JophielState {
  webConfig: WebConfigState;
  role: RoleState;
  profile: ProfileState;
}

export const jophielReducer = combineReducers<JophielState>({
  webConfig: webConfigReducer,
  role: roleReducer,
  profile: profileReducer,
});
