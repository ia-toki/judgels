import { TypedAction, TypedReducer } from 'redoodle';

import { JophielRole } from 'modules/api/jophiel/role';
import { UserWebConfig } from 'modules/api/jophiel/userWeb';

export interface UserWebState {
  config: UserWebConfig;
  isConfigLoaded?: boolean;
}

export const INITIAL_STATE: UserWebState = {
  config: {
    role: JophielRole.Guest,
  },
};

export const PutWebConfig = TypedAction.define('jophiel/userWeb/PUT_CONFIG')<UserWebConfig>();

function createUserWebReducer() {
  const builder = TypedReducer.builder<UserWebState>();

  builder.withHandler(PutWebConfig.TYPE, (state, payload) => ({ config: payload, isConfigLoaded: true }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const userWebReducer = createUserWebReducer();
