import { setWith, TypedAction, TypedReducer } from 'redoodle';

import { ProblemSetProblem } from '../../../../../../modules/api/jerahmeel/problemSetProblem';

export interface ProblemSetProblemState {
  value?: ProblemSetProblem;
}

export const INITIAL_STATE: ProblemSetProblemState = {};

export const PutProblemSetProblem = TypedAction.define('jerahmeel/problemSetProblem/PUT')<ProblemSetProblem>();
export const DelProblemSetProblem = TypedAction.defineWithoutPayload('jerahmeel/problemSetProblem/DEL')();

function createProblemSetProblemReducer() {
  const builder = TypedReducer.builder<ProblemSetProblemState>();

  builder.withHandler(PutProblemSetProblem.TYPE, (state, payload) => setWith(state, { value: payload }));
  builder.withHandler(DelProblemSetProblem.TYPE, () => ({ value: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const problemSetProblemReducer = createProblemSetProblemReducer();
