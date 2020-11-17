import { TypedAction, TypedReducer } from 'redoodle';

export interface ProfileState {
  userJid?: string;
  username?: string;
}

export const INITIAL_STATE: ProfileState = {};

export const PutUser = TypedAction.define('jophiel/profile/PUT')<{
  userJid: string;
  username: string;
}>();
export const DelUser = TypedAction.defineWithoutPayload('jophiel/profile/DEL')();

function createProfileReducer() {
  const builder = TypedReducer.builder<ProfileState>();

  builder.withHandler(PutUser.TYPE, (state, payload) => payload);
  builder.withHandler(DelUser.TYPE, () => ({ userJid: undefined, username: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const profileReducer = createProfileReducer();
