import { combineReducers } from 'redux';

import { webConfigReducer, WebConfigState } from './webConfigReducer';
import { roleReducer, RoleState } from './roleReducer';
import { publicProfileReducer, PublicProfileState } from '../profiles/modules/publicProfileReducer';

export interface JophielState {
  webConfig: WebConfigState;
  role: RoleState;
  publicProfile: PublicProfileState;
}

export const jophielReducer = combineReducers<JophielState>({
  webConfig: webConfigReducer,
  role: roleReducer,
  publicProfile: publicProfileReducer,
});
