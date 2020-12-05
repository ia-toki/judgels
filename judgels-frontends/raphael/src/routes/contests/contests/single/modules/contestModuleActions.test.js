import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../utils/nock';
import * as contestModuleActions from './contestModuleActions';

const contestJid = 'contestJid';
const mockStore = configureMockStore([thunk]);

describe('contestModuleActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getModules()', () => {
    const responseBody = ['REGISTRATION'];

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/${contestJid}/modules`)
        .reply(200, responseBody);

      const response = await store.dispatch(contestModuleActions.getModules(contestJid));
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

      await store.dispatch(contestModuleActions.enableModule(contestJid, 'REGISTRATION'));
    });
  });

  describe('disableModule()', () => {
    it('calls API', async () => {
      nockUriel()
        .options(`/contests/${contestJid}/modules/REGISTRATION`)
        .reply(200)
        .delete(`/contests/${contestJid}/modules/REGISTRATION`)
        .reply(200);

      await store.dispatch(contestModuleActions.disableModule(contestJid, 'REGISTRATION'));
    });
  });

  describe('getConfig()', () => {
    const responseBody = {
      virtual: {},
    };

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/${contestJid}/modules/config`)
        .reply(200, responseBody);

      const response = await store.dispatch(contestModuleActions.getConfig(contestJid));
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

      await store.dispatch(contestModuleActions.upsertConfig(contestJid, config));
    });
  });
});
