import { TypedAction, TypedReducer } from 'redoodle';

import { ContestWebConfig } from 'modules/api/uriel/contestWeb';

export interface ContestWebConfigState {
  value?: ContestWebConfig;
}

export const INITIAL_STATE: ContestWebConfigState = {};

export const PutWebConfig = TypedAction.define('uriel/contest/web/PUT_CONFIG')<ContestWebConfig>();
export const DelWebConfig = TypedAction.defineWithoutPayload('uriel/contest/web/DEL_CONFIG')();

function createContestWebConfigReducer() {
  const builder = TypedReducer.builder<ContestWebConfigState>();

  builder.withHandler(PutWebConfig.TYPE, (state, payload) => ({ value: payload }));
  builder.withHandler(DelWebConfig.TYPE, () => ({ value: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const contestWebConfigReducer = createContestWebConfigReducer();
