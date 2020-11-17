import { push } from 'connected-react-router';
import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../../conf';
import * as changePasswordActions from './changePasswordActions';

const oldPassword = 'oldPass';
const newPassword = 'newPass';
const mockStore = configureMockStore([thunk]);

describe('changePasswordActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('updateMyPassword()', () => {
    describe('when the old password is correct', () => {
      it('tries to change password', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/users/me/password`)
          .reply(200)
          .post(`/users/me/password`, { oldPassword, newPassword })
          .reply(200);

        await store.dispatch(changePasswordActions.updateMyPassword(oldPassword, newPassword));
        expect(store.getActions()).toContainEqual(push('/account/info'));
      });
    });

    describe('when the old password is incorrect', () => {
      it('throws a more descriptive error', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/users/me/password`)
          .reply(200)
          .post(`/users/me/password`, { oldPassword, newPassword })
          .reply(400);

        await expect(store.dispatch(changePasswordActions.updateMyPassword(oldPassword, newPassword))).rejects.toEqual(
          new Error('Incorrect old password.')
        );
      });
    });
  });
});
