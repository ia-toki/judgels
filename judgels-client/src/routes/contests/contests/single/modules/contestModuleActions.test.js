import nock from 'nock';

import { nockUriel } from '../../../../../utils/nock';

import * as contestModuleActions from './contestModuleActions';

const contestJid = 'contestJid';

describe('contestModuleActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getModules()', () => {
    const responseBody = ['REGISTRATION'];

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/modules`).reply(200, responseBody);

      const response = await contestModuleActions.getModules(contestJid);
      expect(response).toEqual(responseBody);
    });
  });

  describe('enableModule()', () => {
    it('calls API', async () => {
      nockUriel()
        .options(`/contests/${contestJid}/modules/REGISTRATION`)
        .reply(200)
        .put(`/contests/${contestJid}/modules/REGISTRATION`)
        .reply(200);

      await contestModuleActions.enableModule(contestJid, 'REGISTRATION');
    });
  });

  describe('disableModule()', () => {
    it('calls API', async () => {
      nockUriel()
        .options(`/contests/${contestJid}/modules/REGISTRATION`)
        .reply(200)
        .delete(`/contests/${contestJid}/modules/REGISTRATION`)
        .reply(200);

      await contestModuleActions.disableModule(contestJid, 'REGISTRATION');
    });
  });

  describe('getConfig()', () => {
    const responseBody = {
      virtual: {},
    };

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/modules/config`).reply(200, responseBody);

      const response = await contestModuleActions.getConfig(contestJid);
      expect(response).toEqual(responseBody);
    });
  });

  describe('upsertConfig()', () => {
    const config = {
      virtual: {},
    };

    it('calls API', async () => {
      nockUriel()
        .options(`/contests/${contestJid}/modules/config`)
        .reply(200)
        .put(`/contests/${contestJid}/modules/config`, config)
        .reply(200);

      await contestModuleActions.upsertConfig(contestJid, config);
    });
  });
});
