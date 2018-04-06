import { TypedAction, TypedReducer } from 'redoodle';

import { JophielRole } from '../../../modules/api/jophiel/my';

export interface RoleState {
  value: JophielRole;
}

export const INITIAL_STATE: RoleState = {
  value: JophielRole.Guest,
};

export const PutRole = TypedAction.define('jophiel/role/PUT')<JophielRole>();

function createRoleReducer() {
  const builder = TypedReducer.builder<RoleState>();

  builder.withHandler(PutRole.TYPE, (state, payload) => ({ value: payload }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const roleReducer = createRoleReducer();
