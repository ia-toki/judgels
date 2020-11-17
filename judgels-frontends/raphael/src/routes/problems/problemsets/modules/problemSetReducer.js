import { setWith, TypedAction, TypedReducer } from 'redoodle';

import { ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';

export interface ProblemSetState {
  value?: ProblemSet;
}

export const INITIAL_STATE: ProblemSetState = {};

export const PutProblemSet = TypedAction.define('jerahmeel/problemSet/PUT')<ProblemSet>();
export const DelProblemSet = TypedAction.defineWithoutPayload('jerahmeel/problemSet/DEL')();

function createProblemSetReducer() {
  const builder = TypedReducer.builder<ProblemSetState>();

  builder.withHandler(PutProblemSet.TYPE, (state, payload) => setWith(state, { value: payload }));
  builder.withHandler(DelProblemSet.TYPE, () => ({ value: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const problemSetReducer = createProblemSetReducer();
