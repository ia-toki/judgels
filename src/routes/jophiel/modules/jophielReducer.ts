import { combineReducers } from 'redux';

import { webConfigReducer, WebConfigState } from './webConfigReducer';
import { roleReducer, RoleState } from './roleReducer';

export interface JophielState {
  webConfig: WebConfigState;
  role: RoleState;
}

export const jophielReducer = combineReducers<JophielState>({
  webConfig: webConfigReducer,
  role: roleReducer,
});
