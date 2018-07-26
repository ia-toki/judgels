import { TypedAction, TypedReducer } from 'redoodle';

export interface ProfileState {
  userJid?: string;
}

export const INITIAL_STATE: ProfileState = {};

export const PutUserJid = TypedAction.define('jophiel/profile/PUT')<string>();
export const DelUserJid = TypedAction.defineWithoutPayload('jophiel/profile/DEL')();

function createProfileReducer() {
  const builder = TypedReducer.builder<ProfileState>();

  builder.withHandler(PutUserJid.TYPE, (state, payload) => ({ userJid: payload }));
  builder.withHandler(DelUserJid.TYPE, () => ({ userJid: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const profileReducer = createProfileReducer();
