import { combineReducers } from 'redux';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';

import { contestReducer, ContestState } from '../contests/modules/contestReducer';
import { contestWebConfigReducer, ContestWebConfigState } from '../contests/modules/contestWebConfigReducer';
import { ContestDescriptionState } from '../contests/modules/contestDescriptionReducer';

export interface UrielState {
  contest: ContestState;
  contestWebConfig: ContestWebConfigState;
  contestDescription: ContestDescriptionState;
}

export const urielReducer = combineReducers<UrielState>({
  contest: persistReducer({ key: 'urielContest', storage }, contestReducer),
  contestWebConfig: contestWebConfigReducer,
});
