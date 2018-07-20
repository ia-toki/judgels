import { TypedAction, TypedReducer } from 'redoodle';

import { PublicUserProfile } from '../../../modules/api/jophiel/userProfile';

export interface PublicProfileState {
  value?: PublicUserProfile;
}

export const INITIAL_STATE: PublicProfileState = {};

export const PutPublicProfile = TypedAction.define('jophiel/publicProfile/PUT')<PublicUserProfile>();
export const DelPublicProfile = TypedAction.defineWithoutPayload('jophiel/publicProfile/profile/DEL')();

function createPublicProfileReducer() {
  const builder = TypedReducer.builder<PublicProfileState>();

  builder.withHandler(PutPublicProfile.TYPE, (state, payload) => ({ value: payload }));
  builder.withHandler(DelPublicProfile.TYPE, () => ({ value: undefined }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
}

export const publicProfileReducer = createPublicProfileReducer();
