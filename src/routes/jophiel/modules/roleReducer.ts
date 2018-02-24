import { TypedAction, TypedReducer } from 'redoodle';

import { Role } from '../../../modules/api/jophiel/user';

export interface RoleState {
  value: Role;
}

export const INITIAL_STATE: RoleState = {
  value: Role.User,
};

export const PutRole = TypedAction.define('jophiel/role/PUT')<Role>();

function createRoleReducer() {
  const builder = TypedReducer.builder<RoleState>();

  builder.withHandler(PutRole.TYPE, (state, payload) => ({ value: payload }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const roleReducer = createRoleReducer();
