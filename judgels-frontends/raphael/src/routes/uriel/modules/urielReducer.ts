import { combineReducers } from 'redux';

import { contestReducer, ContestState } from '../contests/modules/contestReducer';
import { contestWebConfigReducer, ContestWebConfigState } from '../contests/modules/contestWebConfigReducer';

export interface UrielState {
  contest: ContestState;
  contestWebConfig: ContestWebConfigState;
}

export const urielReducer = combineReducers<UrielState>({
  contest: contestReducer,
  contestWebConfig: contestWebConfigReducer,
});
