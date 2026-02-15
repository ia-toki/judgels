import nock from 'nock';

import { nockJophiel } from '../../../utils/nock';

import * as avatarActions from './avatarActions';

const userJid = 'user-jid';

describe('avatarActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('updateAvatar()', () => {
    const file = {};

    it('calls API', async () => {
      nockJophiel().post(`/users/${userJid}/avatar`).reply(200);

      await avatarActions.updateAvatar(userJid, file);
    });
  });

  describe('deleteAvatar()', () => {
    it('calls API', async () => {
      nockJophiel().options(`/users/${userJid}/avatar`).reply(200).delete(`/users/${userJid}/avatar`).reply(200);

      await avatarActions.deleteAvatar(userJid);
    });
  });
});
