import { combineReducers } from 'redux';

import { contestReducer, ContestState } from '../routes/contests/modules/contestReducer';

export interface UrielState {
  contest: ContestState;
}

export const urielReducer = combineReducers<UrielState>({
  contest: contestReducer,
});
