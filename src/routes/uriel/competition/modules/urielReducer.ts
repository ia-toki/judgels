import { combineReducers } from 'redux';

import { contestReducer, ContestState } from './contestReducer';

export interface UrielState {
  contest: ContestState;
}

export const urielReducer = combineReducers<UrielState>({
  contest: contestReducer,
});
