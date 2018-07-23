import { TypedAction, TypedReducer } from 'redoodle';

import { Contest } from '../../../../modules/api/uriel/contest';

export interface ContestState {
  value?: Contest;
}

export const INITIAL_STATE: ContestState = {};

export const PutContest = TypedAction.define('uriel/contest/PUT')<Contest>();
export const DelContest = TypedAction.defineWithoutPayload('uriel/contest/DEL')();

function createContestReducer() {
  const builder = TypedReducer.builder<ContestState>();

  builder.withHandler(PutContest.TYPE, (state, payload) => ({ value: payload }));
  builder.withHandler(DelContest.TYPE, () => ({ value: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const contestReducer = createContestReducer();
