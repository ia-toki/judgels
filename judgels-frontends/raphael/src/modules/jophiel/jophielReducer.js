import { combineReducers } from 'redux';

import { userWebReducer, UserWebState } from '../../routes/jophiel/modules/userWebReducer';
import { profileReducer, ProfileState } from '../../routes/jophiel/modules/profileReducer';
import { webReducer, WebState } from '../../routes/jophiel/modules/webReducer';

export interface JophielState {
  web: WebState;
  userWeb: UserWebState;
  profile: ProfileState;
}

export const jophielReducer = combineReducers<JophielState>({
  userWeb: userWebReducer,
  profile: profileReducer,
  web: webReducer,
});
