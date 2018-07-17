import { combineReducers } from 'redux';

import { webConfigReducer, WebConfigState } from './webConfigReducer';

export interface JophielState {
  webConfig: WebConfigState;
}

export const jophielReducer = combineReducers<JophielState>({
  webConfig: webConfigReducer,
});
