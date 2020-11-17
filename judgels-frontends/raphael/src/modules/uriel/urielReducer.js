import { combineReducers } from 'redux';
import { persistReducer } from 'redux-persist';
import storage from 'redux-persist/es/storage';

import { contestReducer, ContestState } from '../../routes/contests/contests/modules/contestReducer';
import {
  contestWebConfigReducer,
  ContestWebConfigState,
} from '../../routes/contests/contests/modules/contestWebConfigReducer';

export interface UrielState {
  contest: ContestState;
  contestWebConfig: ContestWebConfigState;
}

export const urielReducer = combineReducers<UrielState>({
  contest: persistReducer({ key: 'urielContest', storage }, contestReducer),
  contestWebConfig: contestWebConfigReducer,
});
