import { setWith, TypedAction, TypedReducer } from 'redoodle';

import { Contest } from 'modules/api/uriel/contest';

export interface ContestState {
  value?: Contest;
  isEditing?: boolean;
}

export const INITIAL_STATE: ContestState = {};

export const PutContest = TypedAction.define('uriel/contest/PUT')<Contest>();
export const DelContest = TypedAction.defineWithoutPayload('uriel/contest/DEL')();
export const EditContest = TypedAction.define('uriel/contest/EDIT')<boolean>();

function createContestReducer() {
  const builder = TypedReducer.builder<ContestState>();

  builder.withHandler(PutContest.TYPE, (state, payload) => setWith(state, { value: payload }));
  builder.withHandler(DelContest.TYPE, () => ({ value: undefined }));
  builder.withHandler(EditContest.TYPE, (state, payload) => setWith(state, { isEditing: payload }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const contestReducer = createContestReducer();
