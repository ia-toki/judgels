import { TypedAction, TypedReducer } from 'redoodle';

import { WebConfig } from '../../../modules/api/jophiel/web';

export interface WebState {
  config: WebConfig;
}

export const INITIAL_STATE: WebState = {
  config: {
    announcements: [],
  },
};

export const PutWebConfig = TypedAction.define('jophiel/web/PUT_CONFIG')<WebConfig>();

function createWebReducer() {
  const builder = TypedReducer.builder<WebState>();

  builder.withHandler(PutWebConfig.TYPE, (state, payload) => ({ config: payload }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const webReducer = createWebReducer();
