import { TypedAction, TypedReducer } from 'redoodle';

import { ContestDescription } from 'modules/api/uriel/contest';

export interface ContestDescriptionState {
  value?: ContestDescription;
}

export const INITIAL_STATE: ContestDescriptionState = {};

export const PutContest = TypedAction.define('uriel/contest/PUT')<ContestDescription>();
export const DelContest = TypedAction.defineWithoutPayload('uriel/contest/DEL')();

function createContestDescriptionReducer() {
  const builder = TypedReducer.builder<ContestDescriptionState>();

  builder.withHandler(PutContest.TYPE, (state, payload) => ({ value: payload }));
  builder.withHandler(DelContest.TYPE, () => ({ value: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const contestDescriptionReducer = createContestDescriptionReducer();
