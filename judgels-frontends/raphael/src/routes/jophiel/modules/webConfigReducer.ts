import { TypedAction, TypedReducer } from 'redoodle';

import { WebConfig } from '../../../modules/api/jophiel/web';

export interface WebConfigState {
  value?: WebConfig;
}

export const INITIAL_STATE: WebConfigState = {};

export const PutWebConfig = TypedAction.define('jophiel/webConfig/PUT')<WebConfig>();

function createWebConfigReducer() {
  const builder = TypedReducer.builder<WebConfigState>();

  builder.withHandler(PutWebConfig.TYPE, (state, payload) => ({ value: payload }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const webConfigReducer = createWebConfigReducer();
